package mystrom.mystrom2em;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mystrom.mystrom2em.config.ConfigMyStromSwitch;
import mystrom.mystrom2em.config.Configuration;
import mystrom.mystrom2em.em.MstEMSensorData;
import mystrom.mystrom2em.em.MstEmSensorDataRequest;
import mystrom.mystrom2em.em.SensorData;
import mystrom.mystrom2em.mystrom.MyStromReport;

public class DataFileReader implements Runnable {

	private final static Logger LOG = LoggerFactory.getLogger(DataFileReader.class);
	private static int CHUNCK_SIZE = 60 * 24 * 7;

	private final Map<String, ConfigMyStromSwitch> myStromSwitchConfigs;
	private final BufferedReader fh;
	private final MstEMSensorData em;

	public DataFileReader(final Configuration config, final String dataFile) throws FileNotFoundException {
		myStromSwitchConfigs = config.getSensorConfigs().stream() //
				.collect(Collectors.toMap(ConfigMyStromSwitch::getIp, s -> s));
		em = new MstEMSensorData(config.getEmBaseUrl(), config.getEmApiKey());
		fh = new BufferedReader(new FileReader(dataFile));
	}

	public Collection<MyStromReport> readLines(final int lines) throws IOException, ParseException {
		assert lines > 0 : "positive integer expected";
		assert lines % 2 == 0 : "even integer expeected";
		
		// @TODO Ensure that we always get complete MyStromReport

		final Map<String, MyStromReport> result = new HashMap<>();
		String line;
		int i = 1;
		while ((line = fh.readLine()) != null && i < lines) {
			i++;
			final String[] row = line.split(";");

			final Date date = DataCollector.DATE_FORMAT_SQL.parse(row[0]);
			final String sourceIp = row[1];
			final String sourceType = row[2];
			final double value = Double.parseDouble(row[3]);

			final String key = row[0] + "_" + sourceIp;

			result.computeIfAbsent(key, k -> new MyStromReport(date, sourceIp));

			switch (sourceType.toLowerCase()) {
			case "temp":
				result.get(key).setTemperature(value);
				break;
			case "power":
				result.get(key).setPower(value);
				break;
			default:
				throw new IllegalArgumentException("Unexpected type \"" + sourceType + "\"");
			}
		}

		return result.values();
	}

	@Override
	public void run() {
		try {
			final List<MyStromReport> data = new ArrayList<>();

			while (true) {
				final Collection<MyStromReport> chunk = readLines(CHUNCK_SIZE);
				if (chunk.isEmpty()) {
					return; // Reached end of file
				}
				data.addAll(chunk);

				final MyStromReport lastValue = data.get(data.size() - 1);
				final Date upperTimeRange = DataCollector.toLastQuaterHour(lastValue.getDate());

				try {
					final List<SensorData> sensorData = getSensorData(data, upperTimeRange);
					if (sensorData.isEmpty()) {
						return;
					}
					em.sendData(sensorData, MstEmSensorDataRequest.JUNCTION.MERGE);
					truncateDataPointsUpTo(data, upperTimeRange);
					LOG.info("Send {} sensors to EM", sensorData.size());
				} catch (final IOException e) {
					LOG.error("Unable to send data to EM, cause of: {}", e.getMessage());
				}
			}

		} catch (IOException | ParseException e) {
			LOG.error("Error while reading log file", e);
		}
	}

	private void truncateDataPointsUpTo(List<MyStromReport> data, Date upperTimeRange) {
		data.removeIf(report -> report.isBefore(upperTimeRange));
	}

	private List<SensorData> getSensorData(final Collection<MyStromReport> data, final Date upperTimeRange) {
		final Map<String, List<MyStromReport>> groupedAndFilteredData = data.stream() //
				.filter(report -> report.isBefore(upperTimeRange)) //
				.collect(Collectors.groupingBy(MyStromReport::getSourceIp));

		final List<SensorData> result = new ArrayList<>();
		groupedAndFilteredData.forEach((myStromSwitchIp, filteredReports) -> {
			if (!myStromSwitchConfigs.containsKey(myStromSwitchIp)) {
				return;
			}
			final ConfigMyStromSwitch myStromSwitchConfig = myStromSwitchConfigs.get(myStromSwitchIp);

			final SensorData energy = DataCollector.processReportsToSensorData(myStromSwitchConfig.getAksEnergy(),
					filteredReports, r -> r.getPower() / 4); // W / 4 == 15min
																// Wh
			result.add(energy);
			final SensorData temp = DataCollector.processReportsToSensorData(myStromSwitchConfig.getAksTemp(),
					filteredReports, MyStromReport::getTemperature);
			result.add(temp);
		});

		return result;
	}
}
