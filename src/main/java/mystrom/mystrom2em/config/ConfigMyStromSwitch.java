package mystrom.mystrom2em.config;

public class ConfigMyStromSwitch {
	private final String ip;
	private final String aksTemp;
	private final String aksVerbrauch;

	public ConfigMyStromSwitch(String ip, String aksVerbrauch, String aksTemp) {
		this.ip = ip;
		this.aksTemp = aksTemp;
		this.aksVerbrauch = aksVerbrauch;
	}

	public String geIp() {
		return ip;
	}

	public String getAksTemp() {
		return aksTemp;
	}

	public String getAksVerbrauch() {
		return aksVerbrauch;
	}

}
