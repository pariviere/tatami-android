package tatami.android.ui;

import tatami.android.AppState;
import tatami.android.R;
import tatami.android.R.id;
import tatami.android.R.layout;
import tatami.android.R.menu;
import tatami.android.R.string;
import tatami.android.task.DoLogin;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * <p>
 * </p>
 * 
 * @author pariviere
 */
public class LoginActivity extends Activity {
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		EditText loginText = (EditText) findViewById(R.id.loginText);
		EditText passwdText = (EditText) findViewById(R.id.passwdText);

		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		String login = settings.getString("mail", "jdoe@ippon.fr");
		String passwd = settings.getString("password", "password");

		loginText.setText(login);
		passwdText.setText(passwd);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	/**
	 * 
	 * @param view
	 */
	public void doLogin(View view) {

		AppState app = (AppState) getApplication();

		if (!app.isConnected()) {
			Toast.makeText(this, "No network found", Toast.LENGTH_SHORT).show();
		} else {
			EditText loginText = (EditText) findViewById(R.id.loginText);
			EditText passwdText = (EditText) findViewById(R.id.passwdText);

			String login = loginText.getText().toString();
			String passwd = passwdText.getText().toString();

			SharedPreferences settings = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();

			editor.putString("mail", login);
			editor.putString("password", passwd);

			editor.commit();

			this.progressDialog = ProgressDialog.show(this, null,
					getString(R.string.authentication_in_progress), true);

//			new DoLogin(this).execute(login, passwd);
		}
	}

	/**
	 * 
	 */
	public void startTimeline() {
		Intent intent = new Intent(this, TimelineActivity.class);

		startActivity(intent);
	}

	/**
	 * 
	 */
	public void retryLogin() {
		this.progressDialog.dismiss();

		EditText loginText = (EditText) findViewById(R.id.loginText);
		EditText passwdText = (EditText) findViewById(R.id.passwdText);

		loginText.setError(getString(R.string.access_denied));
		passwdText.setError(getString(R.string.access_denied));

		Toast.makeText(this, getString(R.string.access_denied),
				Toast.LENGTH_LONG).show();
	}
}
