package mystrom.mystrom2em.em;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SensorData {
	
	private String aks;
	private Map<Date, Double> data = new HashMap<>();

	public SensorData(final String aks) {
		this.aks = aks;
	}
	
	public String getAks() {
		return aks;
	}

	public Map<Date, Double> getData() {
		return data;
	}
	
	public void addData(Date date, Double value) {
		data.put(date, value);
	}

	public void setData(Map<Date, Double> values) {
		values.forEach((date, value) -> {
			data.put(date, value);
		});
	}
}
