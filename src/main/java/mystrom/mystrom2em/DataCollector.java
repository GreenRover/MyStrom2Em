package mystrom.mystrom2em;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mystrom.mystrom2em.config.ConfigMyStromSwitch;
import mystrom.mystrom2em.config.Configuration;
import mystrom.mystrom2em.em.MstEMSensorData;
import mystrom.mystrom2em.em.MstEmSensorDataRequest;
import mystrom.mystrom2em.em.SensorData;
import mystrom.mystrom2em.mystrom.MyStrom;
import mystrom.mystrom2em.mystrom.MyStromReport;

public class DataCollector implements Runnable {
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
	private final ThreadPoolExecutor excecuter = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());
	private final MyStrom ms;
	private final MstEMSensorData em;
	private final Configuration config;

	private final Map<ConfigMyStromSwitch, List<MyStromReport>> dataPoints = new HashMap<>();

	private final static Logger LOG = LoggerFactory.getLogger(DataCollector.class);
	private final static Logger DATA_POINTS_LOG = LoggerFactory.getLogger("DATA_POINTS");
	public final static SimpleDateFormat DATE_FORMAT_SQL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public DataCollector(final Configuration config) { // 2018-12-09 14:09:43
		this.config = config;
		em = new MstEMSensorData(config.getEmBaseUrl(), config.getEmApiKey());
		ms = new MyStrom();
	}

	@Override
	public void run() {
		initSensorScheduler();
		initEmPushScheduler();
	}

	private void initSensorScheduler() {
		for (final ConfigMyStromSwitch sensorConfig : config.getSensorConfigs()) {
			dataPoints.putIfAbsent(sensorConfig, Collections.synchronizedList(new ArrayList<>()));
			scheduler.scheduleAtFixedRate(() -> {
				excecuter.execute(() -> collectData(sensorConfig));
			}, 1, sensorConfig.getPullIntervall(), TimeUnit.SECONDS);
		}
	}

	private void collectData(final ConfigMyStromSwitch sensorConfig) {
		try {
			final MyStromReport report = ms.readReport(InetAddress.getByName(sensorConfig.getIp()));
			report.setSourceIp(sensorConfig.getIp());
			dataPoints.get(sensorConfig).add(report);
			logCsv(sensorConfig.getIp(), report);
		} catch (final IOException e) {
			LOG.error("Unable to load data from {}, cause of: {}", sensorConfig.getIp(), e.getMessage());
		}
	}

	private void logCsv(final String ip, final MyStromReport report) {
		final Double power = report.getPower();
		if (power != null) {
			DATA_POINTS_LOG
					.info(String.format("%s;%s;power;%.4f", DATE_FORMAT_SQL.format(report.getDate()), ip, power));
		}

		final Double temp = report.getTemperature();
		if (temp != null) {
			DATA_POINTS_LOG.info(String.format("%s;%s;temp;%.4f", DATE_FORMAT_SQL.format(report.getDate()), ip, temp));
		}
	}

	/**
	 *  Run all 15min at xx:00, xx:15, xx:30, xx:45 (at excact 15min border)
	 */
	private void initEmPushScheduler() {
		final LocalDateTime now = LocalDateTime.now();
		final LocalDateTime nextQuarter = now.truncatedTo(ChronoUnit.HOURS)
				.plusMinutes(15 * ((now.getMinute() / 15) + 1));
		final long delay = (nextQuarter.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
				- System.currentTimeMillis()) / 1000;
		scheduler.scheduleAtFixedRate(() -> {
			excecuter.execute(() -> emPushAndTruncateDataPoints());
		}, delay, 15 * 60, TimeUnit.SECONDS);
	}

	private void emPushAndTruncateDataPoints() {
		final Date upperTimeRange = toLastQuaterHour(new Date());

		try {
			final Collection<SensorData> sensorData = getSensorData(upperTimeRange);
			if (sensorData.isEmpty()) {
				return;
			}
			em.sendData(sensorData, MstEmSensorDataRequest.JUNCTION.APPEND);
			truncateDataPointsUpTo(upperTimeRange);
			LOG.info("Send {} sensors to EM", sensorData.size());
		} catch (final IOException e) {
			LOG.error("Unable to send data to EM, cause of: {}", e.getMessage());
		}
	}

	private Collection<SensorData> getSensorData(final Date upperTimeRange) {
		return dataPoints.entrySet().stream() //
				.flatMap(e -> {
					final List<MyStromReport> filteredReports = e.getValue().stream() //
							.filter(report -> report.isBefore(upperTimeRange)) //
							.collect(Collectors.toList());

					final SensorData energy = processReportsToSensorData(e.getKey().getAksEnergy(), filteredReports,
							r -> r.getPower() / 4); // W / 4 == 15min Wh
					final SensorData temp = processReportsToSensorData(e.getKey().getAksTemp(), filteredReports,
							MyStromReport::getTemperature);

					return Stream.of(energy, temp);
				}) //
				.filter(Objects::nonNull) //
				.collect(Collectors.toList());
	}

	public static SensorData processReportsToSensorData(final String aks, final List<MyStromReport> reports,
			final ToDoubleFunction<MyStromReport> valueExtractor) {
		if (StringUtils.isEmpty(aks)) {
			return null;
		}

		final Map<Date, Double> data = aggragateValuesTo15m(reports, valueExtractor);
		if (data.isEmpty()) {
			return null;
		}

		final SensorData sensorData = new SensorData(aks);
		sensorData.setData(data);
		return sensorData;
	}

	private static Map<Date, Double> aggragateValuesTo15m(final List<MyStromReport> reports,
			final ToDoubleFunction<MyStromReport> valueExtractor) {
		return reports.stream() //
				.collect(Collectors.groupingBy( //
						report -> toLastQuaterHour(report.getDate()), //
						Collectors.averagingDouble(valueExtractor)));
	}

	public static Date toLastQuaterHour(final Date d) {
		final Calendar gval = Calendar.getInstance();
		gval.setTime(d);
		gval.set(Calendar.MINUTE, 15 * (gval.get(Calendar.MINUTE) / 15));
		gval.set(Calendar.SECOND, 0);
		gval.set(Calendar.MILLISECOND, 0);

		return gval.getTime();
	}

	/**
	 * Delete all data points from cache, cached before upperTimeRange
	 * @param upperTimeRange
	 */
	private void truncateDataPointsUpTo(final Date upperTimeRange) {
		dataPoints.forEach((sensorConfig, reports) -> {
			reports.removeIf(report -> report.isBefore(upperTimeRange));
		});
	}

}
