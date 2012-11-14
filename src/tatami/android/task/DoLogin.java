package tatami.android.task;

import tatami.android.Client;
import tatami.android.LoginActivity;
import android.os.AsyncTask;

/**
 * 
 * @author pariviere
 *
 */
public class DoLogin extends AsyncTask<String, Void, Boolean> {
	
	private final LoginActivity loginActivity;
	
	public DoLogin(LoginActivity loginActivity) {
		this.loginActivity = loginActivity;
	}
	
	
	@Override
	protected Boolean doInBackground(String... params) {
		try {
			boolean authentificated = Client.getInstance().authenticate(
					params[0], params[1]);

			return authentificated;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean authenticated) {
		if (authenticated) {
			loginActivity.startTimeline();
		} else {
			loginActivity.retryLogin();
		}
	}
}
