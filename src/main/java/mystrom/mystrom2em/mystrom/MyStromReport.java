package mystrom.mystrom2em.mystrom;

import java.util.Date;

public class MyStromReport {
	private Date date;
	private Double power;
	private Double temperature;
	private Boolean relay;

	public MyStromReport() {
		date = new Date();
	}

	public Double getPower() {
		return power;
	}

	public void setPower(final Double power) {
		this.power = power;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(final Double temperature) {
		this.temperature = temperature;
	}

	public Boolean getRelay() {
		return relay;
	}

	public void setRelay(final Boolean releay) {
		this.relay = releay;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public boolean isBefore(final Date upperTimeRange) {
		return this.getDate().compareTo(upperTimeRange) < 0;
	}

	@Override
	public String toString() {
		return String.format("date: %s, power: %.3f, temp: %.3f, releay: %s", date.toString(), power, temperature,
				Boolean.TRUE.equals(relay) ? "yes" : "no");
	}
}
