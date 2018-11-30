package mystrom.mystrom2em;

import org.apache.commons.configuration.ConfigurationException;

import mystrom.mystrom2em.config.Configuration;

public class CliApp {
	public static void main(final String[] args) throws ConfigurationException {
		assert args.length == 1 : "Usage: java -ea -jar target/mystrom2em-jar-with-dependencies.jar src/main/resources/config.xml";
		final Configuration config = new Configuration(args[0]);
		final DataCollector app = new DataCollector(config);
		app.run();
	}

}
