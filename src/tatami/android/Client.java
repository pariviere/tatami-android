package tatami.android;

import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import tatami.android.model.Status;

/**
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

	private Status lastStatus;

	public Status getLastStatus() {
		return lastStatus;
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
		HttpURLConnection authConnection = (HttpURLConnection) auth
				.openConnection();

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
		HttpURLConnection timelineConnection = (HttpURLConnection) timeline
				.openConnection();

		timelineConnection.addRequestProperty("Accept", "application/json");

		try {
			String timelinecontent = IOUtils.toString(timelineConnection
					.getInputStream());

			JSONArray jsonArray = new JSONArray(timelinecontent);

			List<Status> statuses = jsonToStatuses(jsonArray);

			if (statuses.size() > 0) {
				Status relativeLastStatus = statuses.get(0);

				if (lastStatus == null) {
					lastStatus = relativeLastStatus;
				} else {
					if (lastStatus.getStatusDate().before(
							relativeLastStatus.getStatusDate())) {
						lastStatus = relativeLastStatus;
					}
				}
			}
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
		HttpURLConnection homeConnection = (HttpURLConnection) home
				.openConnection();
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
			status.setContent(object.getString("content"));
			status.setFirstName(object.getString("firstName"));

			long epoch = object.getLong("statusDate");
			status.setStatusDate(new Date(epoch));

			statuses.add(status);
		}

		return statuses;
	}
}
