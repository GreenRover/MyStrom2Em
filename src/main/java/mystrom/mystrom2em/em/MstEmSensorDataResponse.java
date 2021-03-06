package mystrom.mystrom2em.em;

import java.util.HashMap;
import java.util.Map;

public class MstEmSensorDataResponse {
	private String status;
	private String message;
	private Map<String, MstEmSensorDataResponseSensor> sensors = new HashMap<>();

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, MstEmSensorDataResponseSensor> getSensors() {
		return sensors;
	}

	public void setSensors(Map<String, MstEmSensorDataResponseSensor> sensors) {
		this.sensors = sensors;
	}

	public int importedValues() {
		return sensors.values().stream() //
				.mapToInt(MstEmSensorDataResponseSensor::getImported_values) //
				.sum();
	}

	public boolean allOk() {
		if (!"OK".equals(status)) {
			return false;
		}

		return sensors.values().stream() //
				.map(MstEmSensorDataResponseSensor::getStatus) //
				.allMatch(status -> "OK".equals(status));
	}

	@Override
	public String toString() {
		return String.format("status=%s, msg=%s, sensors=[%s]", status, message, sensors.values().toString());
	}
}

class MstEmSensorDataResponseSensor {
	private String status;
	private String message;
	private Integer imported_values;
	private String last_value_before;
	private String last_value_after;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getImported_values() {
		return imported_values;
	}

	public void setImported_values(Integer imported_values) {
		this.imported_values = imported_values;
	}

	public String getLast_value_before() {
		return last_value_before;
	}

	public void setLast_value_before(String last_value_before) {
		this.last_value_before = last_value_before;
	}

	public String getLast_value_after() {
		return last_value_after;
	}

	public void setLast_value_after(String last_value_after) {
		this.last_value_after = last_value_after;
	}

	@Override
	public String toString() {
		return String.format("status=%s, msg=%s, importedVals=%d, lastBefore=%s, lastAfter=%s", status, message,
				imported_values, last_value_before, last_value_after);
	}

}