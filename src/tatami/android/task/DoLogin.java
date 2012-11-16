package tatami.android.task;

import tatami.android.Client;
import android.os.AsyncTask;

/**
 * <p>
 * </p>
 * 
 * @author pariviere
 */
public class DoLogin extends AsyncTask<String, Void, Boolean> {
	private final LoginListener loginListener;

	public DoLogin(LoginListener loginListener) {
		this.loginListener = loginListener;
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
			loginListener.onLoginSucceed();
		} else {
			loginListener.onLoginFailed();
		}
	}
}
