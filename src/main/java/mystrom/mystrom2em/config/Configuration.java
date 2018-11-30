package mystrom.mystrom2em.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;

public class Configuration {

	private final XMLConfiguration config;

	public Configuration(final String xmlFile) throws ConfigurationException {
		config = new XMLConfiguration(xmlFile);
	}

	public String getEmBaseUrl() {
		final String string = config.getString("em.baseUrl");
		assert StringUtils.isNotEmpty(string) : "Configuration must contain em baseUrl";
		return string;
	}

	public String getEmApiKey() {
		final String string = config.getString("em.apiKey");
		assert StringUtils.isNotEmpty(string) : "Configuration must contain em apiKey";
		assert string.length() == 32 : "The configured apiKey has to be 32 chars length";
		
		return string;
	}

	public List<ConfigMyStromSwitch> getSensorConfigs() {
		final List<ConfigMyStromSwitch> configs = new ArrayList<>();
		final List<HierarchicalConfiguration> switches = config.configurationsAt("mystrom.switch");
		for (final HierarchicalConfiguration sub : switches) {
			configs.add(new ConfigMyStromSwitch( //
					sub.getString("ip"), //
					sub.getInteger("pullIntervall", 30), //
					sub.getString("aksEnergy"), //
					sub.getString("aksTemp")));
		}

		return configs;
	}
}
