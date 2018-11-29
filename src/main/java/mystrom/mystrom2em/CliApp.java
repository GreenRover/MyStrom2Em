package mystrom.mystrom2em;

import org.apache.commons.configuration.ConfigurationException;

import mystrom.mystrom2em.config.Configuration;

public class CliApp {
	public static void main(final String[] args) throws ConfigurationException {
		assert args.length == 1 : "Usage: java my.jar -- config.xml";
		final Configuration config = new Configuration(args[0]);
		final DataCollector app = new DataCollector(config);
		app.run();
	}

}
