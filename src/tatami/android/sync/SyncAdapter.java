package tatami.android.sync;

import java.util.List;

import tatami.android.Client;
import tatami.android.Constants;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * <p>
 * Implementation of Android {@link AbstractThreadedSyncAdapter} API in order to
 * bring synchronization to Tatami.
 * </p>
 * 
 * @author pariviere
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private final static String TAG = SyncAdapter.class.getName();

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);

		Log.d(TAG, "SyncAdapter constructor");
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {

		Log.d(TAG, "Launch synchronization for account " + account.name);
		
		String login = account.name;
		String passwd = AccountManager.get(super.getContext()).getPassword(
				account);

		try {
			Log.d(TAG, "Launch authentication with " + login + ":" + passwd);
			boolean authenticate = Client.getInstance().authenticate(login,
					passwd);

			Log.d(TAG, "Authenticate = " + authenticate);

			List<Status> statuses = Client.getInstance().getTimeline();

			for (Status status : statuses) {
				ContentValues statusValues = StatusFactory.to(status);

				Uri.Builder fullUri = new Uri.Builder();
				fullUri.scheme("content");
				fullUri.authority(Constants.AUTHORITY);
				fullUri.appendPath("status");

				Uri newUri = provider.insert(fullUri.build(), statusValues);

				Log.d(TAG, newUri.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG, ex.getMessage(), ex);
		}

	}
}
