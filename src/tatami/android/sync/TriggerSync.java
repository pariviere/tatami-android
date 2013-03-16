package tatami.android.sync;

import tatami.android.Constants;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.util.Log;

/**
 * <p>
 * Implementation of {@link IntentService} which just trigger sync request for
 * Tatami content provider to Android framework.
 * </p>
 * 
 * @author pariviere
 */
public class TriggerSync extends IntentService {
	private final static String TAG = TriggerSync.class.getSimpleName();

	public TriggerSync() {
		super(TriggerSync.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		AccountManager accountManager = AccountManager.get(this);

		// Tatami does not support multiaccount
		// so don't expect this to always works ;)
		for (Account account : accountManager
				.getAccountsByType(Constants.ACCOUNT_TYPE)) {
			Log.i(TAG, "Ask synchronization for account " + account.name);
			ContentResolver.requestSync(account, Constants.AUTHORITY,
					intent.getExtras());
		}
	}
}
