package tatami.android;

import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import tatami.android.model.Status;

import com.github.rjeschke.txtmark.Processor;

/**
 * <p>
 * A simple to use Java client for Tatami REST API.
 * </p>
 * 
 * @author pariviere
 */
public class Client {
	private static Client _client;

	public static Client getInstance() {
		if (_client == null) {
			_client = new Client();
		}

		return _client;
	}

	private String login = null;
	private String passwd = null;

	private CookieManager _cookieManager;

	public CookieManager getCookieManager() {
		return _cookieManager;
	}

	private Client() {
		this._cookieManager = new CookieManager();
		CookieHandler.setDefault(_cookieManager);
	}

	/**
	 * <p>
	 * Factory method for {@link HttpURLConnection} instantiation. It disable
	 * SSL certs checking in case of HTTPS scheme.
	 * </p>
	 * 
	 * @see Client#certAlwaysTrust
	 * @see Client#hostAlwaysValid
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	protected HttpURLConnection getHttpURLConnection(URL url) throws Exception {

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		if (connection instanceof HttpsURLConnection) {
			// Install the all-trusting trust manager
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, certAlwaysTrust,
					new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
					.getSocketFactory());

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(hostAlwaysValid);
		}

		connection.setConnectTimeout(30000);
		connection.setReadTimeout(30000);

		return connection;
	}

	/**
	 * 
	 * @param login
	 * @param passwd
	 * @return
	 * @throws Exception
	 */
	public boolean authenticate(String login, String passwd) throws Exception {
		this.login = login;
		this.passwd = passwd;

		String loginParameters = String.format("j_username=%s&j_password=%s",
				URLEncoder.encode(login, "UTF-8"),
				URLEncoder.encode(passwd, "UTF-8"));

		URL auth = new URL(ClientURL.AUTH);

		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection authConnection = getHttpURLConnection(auth);

		try {
			authConnection.setDoOutput(true);
			authConnection.setFixedLengthStreamingMode(loginParameters
					.getBytes().length);

			IOUtils.write(loginParameters, authConnection.getOutputStream());

			if (authConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
				String location = authConnection.getHeaderField("Location");

				if (location != null && location.startsWith(ClientURL.LOGIN)) {
					return false;
				} else {
					return true;
				}
			}
			return false;
		} finally {
			authConnection.disconnect();
		}
	}

	public void doAuthenticateIfNecessary() throws Exception {
		if (!isAuthentificated()) {
			if (login == null || passwd == null) {
				throw new Exception("Client not fully initialized");
			} else {
				boolean success = authenticate(login, passwd);

				if (!success) {
					throw new Exception("Can not reauth client...");
				}
			}
		}
	}

	public List<Status> getDetails(String statusId) throws Exception {
		doAuthenticateIfNecessary();

		URL details = new URL(String.format(ClientURL.DETAILS, statusId));
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection discussionConnection = getHttpURLConnection(details);

		discussionConnection.addRequestProperty("Accept", "application/json");

		try {
			String detailsContent = IOUtils.toString(discussionConnection
					.getInputStream());

			JSONObject jsonObj = new JSONObject(detailsContent);
			JSONArray jsonArray = jsonObj.getJSONArray("discussionStatuses");

			List<Status> statuses = jsonToStatuses(jsonArray);

			return statuses;
		} finally {
			discussionConnection.disconnect();
		}
	}

	public boolean discussion(Status status, String statusId) throws Exception {
		doAuthenticateIfNecessary();

		URL discussion = new URL(ClientURL.DISCUSSION);
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection discussionConnection = getHttpURLConnection(discussion);

		discussionConnection.addRequestProperty("Content-Type",
				"application/json");
		discussionConnection.addRequestProperty("Accept",
				"application/json, text/javascript");

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("content", status.getContent());
		jsonObject.put("statusId", statusId);

		String json = jsonObject.toString();

		try {
			discussionConnection.setDoOutput(true);
			discussionConnection
					.setFixedLengthStreamingMode(json.getBytes().length);

			IOUtils.write(json, discussionConnection.getOutputStream());

			if (discussionConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {

			discussionConnection.disconnect();
		}
	}

	public boolean update(Status status) throws Exception {
		doAuthenticateIfNecessary();

		URL update = new URL(ClientURL.UPDATE);
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection updateConnection = getHttpURLConnection(update);

		updateConnection.addRequestProperty("Content-Type", "application/json");
		updateConnection.addRequestProperty("Accept",
				"application/json, text/javascript");

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("content", status.getContent());
		jsonObject.put("groupId", "");

		String json = jsonObject.toString();

		try {
			updateConnection.setDoOutput(true);
			updateConnection
					.setFixedLengthStreamingMode(json.getBytes().length);

			IOUtils.write(json, updateConnection.getOutputStream());

			if (updateConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {

			updateConnection.disconnect();
		}
	}

	public List<Status> getTimeline(SimpleEntry<String, String>... params)
			throws Exception {

		doAuthenticateIfNecessary();

		StringBuilder builder = new StringBuilder("?");

		for (SimpleEntry<String, String> param : params) {
			if (param != null) {
				builder.append(param.getKey());
				builder.append("=");
				builder.append(param.getValue());
			}
		}

		URL timeline = new URL(ClientURL.TIMELINE + builder.toString());

		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection timelineConnection = getHttpURLConnection(timeline);

		timelineConnection.addRequestProperty("Accept", "application/json");

		try {
			String timelinecontent = IOUtils.toString(timelineConnection
					.getInputStream());

			JSONArray jsonArray = new JSONArray(timelinecontent);

			List<Status> statuses = jsonToStatuses(jsonArray);

			return statuses;
		} finally {
			timelineConnection.disconnect();
		}
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isAuthentificated() throws Exception {

		URL home = new URL(ClientURL.ROOT);

		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection homeConnection = getHttpURLConnection(home);
		homeConnection.addRequestProperty("Accept", "text/html");

		try {
			InputStream stream = homeConnection.getInputStream();

			if (homeConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
				return false;
			}

		} finally {
			homeConnection.disconnect();
		}

		return false;
	}

	public static List<Status> jsonToStatuses(JSONArray jsonArray)
			throws Exception {
		List<Status> statuses = new ArrayList<Status>();

		for (int index = 0; index < jsonArray.length(); index++) {
			JSONObject object = jsonArray.getJSONObject(index);

			Status status = new Status();
			status.setStatusId(object.getString("statusId"));
			status.setLastName(object.getString("lastName"));
			status.setUsername(object.getString("username"));
			status.setGravatar(object.getString("gravatar"));
			status.setFirstName(object.getString("firstName"));

			String content = object.getString("content");
			status.setContent(content);
			status.setHtmlContent(Processor.process(content.replaceAll("#",
					"\\\\#")));

			long epoch = object.getLong("statusDate");
			status.setStatusDate(new Date(epoch));

			statuses.add(status);
		}

		return statuses;
	}

	/**
	 * <p>
	 * Implementation of {@link HostnameVerifier} which always approve remote
	 * hostname.
	 * </p>
	 */
	private HostnameVerifier hostAlwaysValid = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * <p>
	 * Implementation of {@link X509TrustManager} which disable any kind of
	 * checking.
	 * </p>
	 */
	private TrustManager[] certAlwaysTrust = new TrustManager[] { new X509TrustManager() {

		@Override
		public void checkClientTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {

		}

		@Override
		public void checkServerTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
				throws CertificateException {

		}

		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	} };
}
