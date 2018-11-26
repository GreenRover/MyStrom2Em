package mystrom.mystrom2em.em;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class SensorData {
	
	private String aks;
	private Map<Date, Float> data = new HashMap<>();

	public SensorData(final String aks) {
		this.aks = aks;
	}
	
	public String getAks() {
		return aks;
	}

	public Map<Date, Float> getData() {
		return data;
	}
	
	public void addData(Date date, Float value) {
		data.put(date, value);
	}
}
