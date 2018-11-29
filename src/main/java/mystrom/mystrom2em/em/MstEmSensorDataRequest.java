package mystrom.mystrom2em.em;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MstEmSensorDataRequest {
	
	public enum JUNCTION {
		APPEND, MERGE
	};

	
	private final String timezone = "UTC";
	private final String date_format = "d.m.Y H:i";
	private String junction = "append";
	private final Map<String, Map<String, Double>> sensors = new HashMap<>();

	private final static SimpleDateFormat DATE_FORMAT_LOCAL = new SimpleDateFormat("dd.MM.YYYY HH:mm");
	static {
		DATE_FORMAT_LOCAL.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public String getJunction() {
		return junction;
	}

	public void setJunction(final JUNCTION junction) {
		this.junction = (junction == JUNCTION.APPEND ? "append" : "merge");
	}

	public Map<String, Map<String, Double>> getSensors() {
		return sensors;
	}

	public void addSensorsData(final String aks, final Map<Date, Double> sensors) {
		this.sensors.putIfAbsent(aks, new HashMap<>());
		sensors.forEach((k, v) -> {
			this.sensors.get(aks) //
					.put(DATE_FORMAT_LOCAL.format(k), v);
		});
	}

	public String getTimezone() {
		return timezone;
	}

	public String getDateFormat() {
		return date_format;
	}
}
