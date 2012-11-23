package tatami.android.task;

import tatami.android.Client;
import android.os.AsyncTask;
import android.util.Log;

/**
 * <p>
 * Implementation of {@link AsyncTask} dedicated to login process.
 * </p>
 * <p>
 * The caller have to implement {@link LoginListener}
 * </p>
 * 
 * @author pariviere
 */
public class DoLogin extends AsyncTask<String, Void, Boolean> {
	private final static String TAG = DoLogin.class.getSimpleName();

	private final LoginListener loginListener;

	public DoLogin(LoginListener loginListener) {
		this.loginListener = loginListener;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String login = params[0];
		String passwd = params[1];

		Log.d(TAG, "Try authentication for user " + login);
		try {
			boolean authentificated = Client.getInstance().authenticate(login,
					passwd);

			return authentificated;
		} catch (Exception ex) {
			Log.e(TAG, "An error was thrown during authentication process", ex);
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
