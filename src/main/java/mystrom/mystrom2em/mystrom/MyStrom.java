package mystrom.mystrom2em.mystrom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;

public class MyStrom {

	public MyStromReport readReport(final InetAddress ip) throws MalformedURLException, IOException {
		final String raw = callHttp(new URL("http://" + ip.getHostAddress() + "/report"));
		
		return new Gson().fromJson(raw, MyStromReport.class);
	}

	private String callHttp(final URL url) throws IOException {
		final StringBuilder result = new StringBuilder();
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}
}
