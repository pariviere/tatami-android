package tatami.android.sync;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import tatami.android.Client;
import tatami.android.R;
import tatami.android.TimelineActivity;
import tatami.android.content.UriBuilder;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
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
	private final static String TAG = SyncAdapter.class.getSimpleName();

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

		Uri fullUri = UriBuilder.getFullUri();

		try {
			SimpleEntry<String, String> queryParams = null;

			boolean autosync = false;

			if (extras.containsKey(SyncMeta.BEFORE_ID)) {
				String beforeStatusId = extras.getString(SyncMeta.BEFORE_ID);

				queryParams = new SimpleEntry<String, String>("max_id",
						beforeStatusId);
			} else {
				autosync = true;

				Status last = queryLastStatus(provider);

				if (last != null) {
					queryParams = new SimpleEntry<String, String>("since_id",
							last.getStatusId());
				}
			}

			Log.d(TAG, "Launch authentication with " + login + ":" + passwd);
			boolean authenticate = Client.getInstance().authenticate(login,
					passwd);

			Log.d(TAG, "Authenticate = " + authenticate);

			List<Status> statuses = Client.getInstance().getTimeline(
					queryParams);

			Log.d(TAG, "Found " + statuses.size()
					+ " statuses with queryParams = " + queryParams);

			for (Status status : statuses) {
				ContentValues statusValues = StatusFactory.to(status);

				provider.insert(fullUri, statusValues);
			}

			if (autosync)
				notifyNewStatus(statuses);
		} catch (Exception ex) {
			getContext().getContentResolver().notifyChange(fullUri, null);

			ex.printStackTrace();
			Log.e(TAG, ex.getMessage(), ex);
		}

	}

	protected void notifyNewStatus(List<Status> statuses) {
		if (statuses.size() > 0) {
			Log.d(TAG, "Send notification to user");
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					this.getContext()).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("Tatami").setContentText(statuses.size() + " new statuses!")
					.setNumber(statuses.size()).setAutoCancel(true);

			Intent resultIntent = new Intent(this.getContext(),
					TimelineActivity.class);

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this
					.getContext());
			stackBuilder.addParentStack(TimelineActivity.class);
			stackBuilder.addNextIntent(resultIntent);

			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

			inboxStyle.setBigContentTitle(statuses.size() + " new statuses!");

			for (Status status : statuses) {

				// / Just the first 30 characters are
				// / shown in notification area
				int last = status.getContent().length();

				if (last > 30) {
					last = 30;
				}

				inboxStyle.addLine(status.getUsername() + ": "
						+ status.getContent().substring(0, last));
			}

			builder.setStyle(inboxStyle);

			builder.setContentIntent(resultPendingIntent);

			NotificationManager manager = (NotificationManager) getContext()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(27272727, builder.build());
		}

	}

	protected Status queryLastStatus(ContentProviderClient provider)
			throws RemoteException {

		Uri lastUri = UriBuilder.getLastStatusUri();

		Cursor cursor = provider.query(lastUri, null, null, null, null);

		if (cursor.moveToFirst())
			return StatusFactory.fromCursorRow(cursor);
		else
			return null;
	}
}
