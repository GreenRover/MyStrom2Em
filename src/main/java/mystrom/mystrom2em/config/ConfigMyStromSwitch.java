package mystrom.mystrom2em.config;

import java.util.Objects;

public class ConfigMyStromSwitch {
	private final String ip;
	private final Integer pullIntervall;
	private final String aksTemp;
	private final String aksEnergy;

	public ConfigMyStromSwitch(final String ip, final Integer pullIntervall, final String aksEnergy, final String aksTemp) {
		assert Objects.nonNull(ip) : "The ip if myStrom switch is required";
		assert Objects.nonNull(aksTemp) || Objects.nonNull(aksEnergy) : "A myStrom switch config need at least an aks for temp or energy";
		
		this.ip = ip;
		this.pullIntervall = pullIntervall;
		this.aksTemp = aksTemp;
		this.aksEnergy = aksEnergy;
	}

	public String geIp() {
		return ip;
	}

	public String getAksTemp() {
		return aksTemp;
	}

	public String getAksEnergy() {
		return aksEnergy;
	}
	
	public Integer getPullIntervall() {
		return pullIntervall;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aksEnergy == null) ? 0 : aksEnergy.hashCode());
		result = prime * result + ((aksTemp == null) ? 0 : aksTemp.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((pullIntervall == null) ? 0 : pullIntervall.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ConfigMyStromSwitch other = (ConfigMyStromSwitch) obj;
		if (aksEnergy == null) {
			if (other.aksEnergy != null)
				return false;
		} else if (!aksEnergy.equals(other.aksEnergy))
			return false;
		if (aksTemp == null) {
			if (other.aksTemp != null)
				return false;
		} else if (!aksTemp.equals(other.aksTemp))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (pullIntervall == null) {
			if (other.pullIntervall != null)
				return false;
		} else if (!pullIntervall.equals(other.pullIntervall))
			return false;
		return true;
	}
}
