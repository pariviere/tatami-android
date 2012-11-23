package tatami.android.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * <p>
 * Entry point for {@link AccountAuthenticator}.
 * </p>
 * 
 * @author pariviere
 */
public class AuthenticationService extends Service {

	private AccountAuthenticator accountAuthenticator;

	@Override
	public void onCreate() {
		super.onCreate();
		this.accountAuthenticator = new AccountAuthenticator(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return this.accountAuthenticator.getIBinder();
	}
}
