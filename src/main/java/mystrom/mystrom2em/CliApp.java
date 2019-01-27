package mystrom.mystrom2em;

import java.io.FileNotFoundException;

import org.apache.commons.configuration.ConfigurationException;

import mystrom.mystrom2em.config.Configuration;

public class CliApp {
	public static void main(final String[] args) throws ConfigurationException {
		assert args.length > 1 : "Usage: java -ea -jar target/mystrom2em-jar-with-dependencies.jar src/main/resources/config.xml";

		if (args.length == 1) {
			final Configuration config = new Configuration(args[0]);
			final DataCollector app = new DataCollector(config);
			app.run();
		} else if (args.length == 2) {
			final Configuration config = new Configuration(args[0]);
			DataFileReader app;
			try {
				app = new DataFileReader(config, args[1]);
				app.run();
			} catch (FileNotFoundException e) {
				System.err.println("Unable to open data file: " + e.getMessage());
				System.exit(1);
			}
		} else {
			System.err.println(
					"Usage: java -ea -jar target/mystrom2em-jar-with-dependencies.jar src/main/resources/config.xml");
		}
	}

}
