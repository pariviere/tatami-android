package tatami.android.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private final static String TAG = SyncAdapter.class.getName();

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		
		Log.d(TAG, "SyncAdapter constructor");
	}
	
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		
		Log.d(TAG, "Sync of " + account.name + " called");
	}

}
