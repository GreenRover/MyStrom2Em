package mystrom.mystrom2em.em;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import mystrom.mystrom2em.em.MstEmSensorDataRequest.JUNCTION;

public class MstEMSensorData {
	private final static Logger LOG = LoggerFactory.getLogger(MstEMSensorData.class);
	
	private final String baseUrl;
	private final String apiKey;

	public MstEMSensorData(final String baseUrl, final String apiKey) {
		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
	}

	public MstEmSensorDataResponse sendData(final Collection<SensorData> data, final JUNCTION junction) throws IOException {
		final String payload = toJson(data, junction);
		final URL url = new URL(baseUrl.replaceAll("\\/$","") + "/energy-manager-sensor-measured/push-raw-data/api_key/" + apiKey);
		final String rawResponse = callHttp(url, payload);
		final MstEmSensorDataResponse response = parseRepsonse(rawResponse);
		
		if (!response.allOk()) {
			LOG.warn(response.toString());
			throw new RuntimeException(String.format("%s: %s", response.getStatus(), response.getMessage()));
		}
		
		return response;
	}

	private String toJson(final Collection<SensorData> data, final JUNCTION junction) {
		final MstEmSensorDataRequest to = new MstEmSensorDataRequest();
		to.setJunction(junction);

		data.stream() //
				.collect(Collectors.groupingBy(SensorData::getAks)) //
				.forEach((k, v) -> {
					for (final SensorData sensor : v) {
						to.addSensorsData(k, sensor.getData());
					}
				});

		return new Gson().toJson(to);
	}

	private String callHttp(final URL url, final String postData) throws IOException {
		final String type = "application/x-www-form-urlencoded";
		final StringBuilder result = new StringBuilder();
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", type);
		conn.setRequestProperty("Content-Length", String.valueOf(postData.length()));
		final OutputStream os = conn.getOutputStream();
		os.write(postData.getBytes());

		final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}
	
	private MstEmSensorDataResponse parseRepsonse(final String rawResponse) {
		return new Gson().fromJson(rawResponse, MstEmSensorDataResponse.class);
	}

}
