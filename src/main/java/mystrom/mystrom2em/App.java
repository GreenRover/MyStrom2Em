package mystrom.mystrom2em;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.ex.ConfigurationException;

import mystrom.mystrom2em.config.ConfigMyStromSwitch;
import mystrom.mystrom2em.config.Configuration;
import mystrom.mystrom2em.em.MstEMSensorData;
import mystrom.mystrom2em.mystrom.MyStrom;
import mystrom.mystrom2em.mystrom.MyStromReport;

/**
 * Hello world!
 *
 */
public class App implements Runnable {
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
	private final ThreadPoolExecutor excecuter = new ThreadPoolExecutor(300, 300, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());
	private MyStrom ms;
	private MstEMSensorData em;
	private Configuration config;

	public static void main(String[] args)
			throws MalformedURLException, UnknownHostException, IOException, ConfigurationException {
		App app = new App();
		app.run();
	}

	public App() throws ConfigurationException {
		config = new Configuration("config.xml");
		em = new MstEMSensorData(config.getEmBaseUrl(), config.getEmApiKey());
		ms = new MyStrom();
	}

	@Override
	public void run() {
		scheduler.scheduleAtFixedRate(() -> {
			try {
				collectData();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, 1, 10, TimeUnit.SECONDS);
	}

	private void collectData() throws MalformedURLException, UnknownHostException, IOException {
		for (ConfigMyStromSwitch sensor : config.getSensorConfigs()) {
			MyStromReport report = ms.readReport(InetAddress.getByName(sensor.geIp()));
			
		}
	}
}
