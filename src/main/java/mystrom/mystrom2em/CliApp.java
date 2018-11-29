package mystrom.mystrom2em;

import org.apache.commons.configuration2.ex.ConfigurationException;

import mystrom.mystrom2em.config.Configuration;

public class CliApp {
	public static void main(final String[] args) throws ConfigurationException {
		final Configuration config = new Configuration(args[0]);
		final DataCollector app = new DataCollector(config);
		app.run();
	}

}
