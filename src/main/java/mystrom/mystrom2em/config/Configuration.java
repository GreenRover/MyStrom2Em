package mystrom.mystrom2em.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.ImmutableNode;

public class Configuration {

	private final XMLConfiguration config;

	public Configuration(final String xmlFile) throws ConfigurationException {
		final Parameters params = new Parameters();
		final FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<>(
				XMLConfiguration.class).configure( //
						params.xml() //
								.setFileName(xmlFile) //
								.setValidating(true) //
								.setSchemaValidation(true) //
		);

		config = builder.getConfiguration();
	}

	public String getEmBaseUrl() {
		return config.getString("ems.baseUrl");
	}

	public String getEmApiKey() {
		return config.getString("ems.apiKey");
	}

	public List<ConfigMyStromSwitch> getSensorConfigs() {
		final List<ConfigMyStromSwitch> configs = new ArrayList<>();
		final List<HierarchicalConfiguration<ImmutableNode>> switches = config.configurationsAt("mystrom.switch");
		for (final HierarchicalConfiguration<ImmutableNode> sub : switches) {
			configs.add(new ConfigMyStromSwitch( //
					sub.getString("name"), //
					sub.getInteger("pullIntervall", 30), //
					sub.getString("aksTemp"), //
					sub.getString("aksEnergy")));
		}

		return configs;
	}
}
