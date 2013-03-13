package tatami.android.request;

import roboguice.util.temp.Ln;
import tatami.android.Client;
import tatami.android.Constants;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.octo.android.robospice.request.SpiceRequest;

/**
 * <p>
 * Class to be implemented by all {@link SpiceRequest} which have to be
 * authenticated to Tatami
 * </p>
 * 
 * @author pariviere
 * 
 * @param <RESULT>
 */
public abstract class AuthenticatedRequest<RESULT> extends SpiceRequest<RESULT> {
	private static String TAG = AuthenticatedRequest.class.getSimpleName();

	private Context context;
	private String login;
	private String passwd;
	private boolean authenticated = false;

	public AuthenticatedRequest(Context context, Class<RESULT> clazz) {
		super(clazz);
		this.context = context;
		init();
	}

	protected void init() {
		AccountManager accManager = AccountManager.get(context);

		Account[] accounts = accManager
				.getAccountsByType(Constants.ACCOUNT_TYPE);

		if (accounts.length == 1) {
			this.login = accounts[0].name;
			this.passwd = AccountManager.get(context).getPassword(accounts[0]);
		}
	}

	@Override
	public RESULT loadDataFromNetwork() throws Exception {

		if (!authenticated) {
			this.authenticated = Client.getInstance().authenticate(login,
					passwd);

			if (authenticated) {
				return doLoadDataFromNetwork();
			}
		}

		Ln.e("Authentication has failed for account %s. Abort", login);
		throw new Exception();
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract RESULT doLoadDataFromNetwork() throws Exception;

	public String getLogin() {
		return login;
	}

	public String getPasswd() {
		return passwd;
	}
}
