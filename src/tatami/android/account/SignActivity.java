package tatami.android.account;

import tatami.android.R;
import tatami.android.task.DoLogin;
import tatami.android.task.LoginListener;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * <p>
 * {@link Activity} to handle sign-in to Tatami server. It will just validate
 * the provide mail and password.
 * </p>
 * 
 * <p>
 * The {@link Activity} itself is a subclass of
 * {@link AccountAuthenticatorActivity} which is meant to handle Android account
 * type integration.
 * </p>
 * 
 * TODO Account edition
 * 
 * @author pariviere
 */
public class SignActivity extends AccountAuthenticatorActivity implements
		LoginListener {
	private static String PARAM_MAIL = "mail";
	private static String PARAM_PASSWD = "passwd";
	private static String TAG = SignActivity.class.getName();
	private AccountManager accountManager;
	private boolean newRequest;
	private String mail;
	private String passwd;

	private EditText mailEditText;
	private EditText passwdEditText;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		accountManager = AccountManager.get(this);

		// This intent can be used for account
		// creation or edition.
		// Extra information are passed by getStringExtra
		final Intent intent = getIntent();
		mail = intent.getStringExtra(PARAM_MAIL);

		newRequest = mail == null;

		if (newRequest) {
			Log.i(TAG, "This is a new sign request.");
		}

		setContentView(R.layout.activity_sign);
		mailEditText = (EditText) findViewById(R.id.mailEditText);
		passwdEditText = (EditText) findViewById(R.id.passwdEditText);
	}

	/**
	 * <p>
	 * Trigger by loginBt
	 * </p>
	 * 
	 * @param view
	 */
	public void onLogin(View view) {
		mail = mailEditText.getText().toString();
		passwd = passwdEditText.getText().toString();

		if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(passwd)) {
			Log.w(TAG, "Mail and password required");
		} else {
			Log.d(TAG, String.format(
					"Proceed authentication : login is '%s', passwd is '%s'",
					mail, passwd));
			DoLogin doLogin = new DoLogin(this);
			doLogin.execute(mail, passwd);
		}
	}

	@Override
	public void onLoginFailed() {
		Log.e(TAG, "Login failed or error found.");
		Toast.makeText(this, "Login failed. Please retry.", Toast.LENGTH_LONG)
				.show();

	}

	@Override
	public void onLoginSucceed() {
		Log.d(TAG, "Login succeed :  save account information");

		final Account account = new Account(this.mail, "tatami.android.account");

		if (newRequest) {
			accountManager.addAccountExplicitly(account, this.passwd, null);
		}

		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, this.mail);
		intent.putExtra(AccountManager.KEY_PASSWORD, this.passwd);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE,
				"tatami.android.account");

		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}
}
