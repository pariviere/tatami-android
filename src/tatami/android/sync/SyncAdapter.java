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
	}

	/**
	 * <p>
	 * </p>
	 */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {

		String login = account.name;
		String passwd = AccountManager.get(super.getContext()).getPassword(
				account);

		Log.d(TAG, "Launch asked for " + login);
		boolean authenticate = false;
		try {
			authenticate = Client.getInstance().authenticate(login, passwd);
		} catch (Exception e) {
			Log.e(TAG, "Unable to handle authentication with Tatami server : "
					+ e.getMessage(), e);
			syncResult.stats.numAuthExceptions++;
			return;
		}

		if (!authenticate) {
			Log.d(TAG, String.format(
					"Authentication for %s rejected by Tatami server", login));
			syncResult.stats.numAuthExceptions++;
			return;
		} else {
			Log.d(TAG,
					"Authentication succeed with Tatami server. Launch sync.");
			
			int syncType = SyncMeta.TYPE_TIMELINE;

			if (extras.containsKey(SyncMeta.TYPE)) {
				syncType = extras.getInt(SyncMeta.TYPE);
			}

			switch (syncType) {
			case SyncMeta.TYPE_DETAILS:
				try {
					Log.d(TAG, "Sync mode is syncDetails");
					doSyncDetails(extras, provider, syncResult, login, passwd);
				} catch (Exception ex) {
					Log.e(TAG,
							String.format("syncDetails have failed : %s",
									ex.getMessage()), ex);
					syncResult.stats.numIoExceptions++;
				}
				break;
			default:
				try {
					Log.d(TAG, "Sync mode is syncTimeline");
					doSyncTimeline(extras, provider, syncResult, login, passwd);
				} catch (Exception ex) {
					Log.e(TAG,
							String.format("syncTimeline have failed : %s",
									ex.getMessage()), ex);
					syncResult.stats.numIoExceptions++;
				}
				break;
			}			
		}
	}

	/**
	 * <p>
	 * Do syncDetails
	 * </p>
	 * 
	 * @param extras
	 * @param provider
	 * @param login
	 * @param passwd
	 * @throws Exception
	 */
	private void doSyncDetails(Bundle extras, ContentProviderClient provider,
			SyncResult syncResult, String login, String passwd)
			throws Exception {

		if (!extras.containsKey(SyncMeta.STATUS_ID))
			return;

		// let's find discussion which refers to currentStatusId
		String currentStatusId = extras.getString(SyncMeta.STATUS_ID);

		List<Status> statuses = Client.getInstance()
				.getDetails(currentStatusId);

		Uri fullUri = UriBuilder.getFullUri();
		Uri detailsUri = UriBuilder.getDetailsUri(currentStatusId);

		for (Status status : statuses) {
			ContentValues statusValues = StatusFactory.to(status);
			provider.insert(fullUri, statusValues);
			
			syncResult.stats.numEntries++;
			syncResult.stats.numInserts++;

			ContentValues detailsValues = new ContentValues();
			detailsValues.put("detailsId", currentStatusId);
			detailsValues.put("statusId", status.getStatusId());
			provider.insert(detailsUri, detailsValues);
			
			syncResult.stats.numInserts++;
		}
	}

	/**
	 * <p>
	 * Do syncTimeline
	 * </p>
	 * 
	 * @param extras
	 * @param provider
	 * @param login
	 * @param passwd
	 * @throws Exception
	 */
	private void doSyncTimeline(Bundle extras, ContentProviderClient provider,
			SyncResult syncResult, String login, String passwd)
			throws Exception {
		Uri fullUri = UriBuilder.getFullUri();

		SimpleEntry<String, String> queryParams = null;

		// timeline is sync either by date (autosync)
		// either against another status
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

		List<Status> statuses = Client.getInstance().getTimeline(queryParams);

		Log.d(TAG, "Found " + statuses.size() + " statuses with queryParams = "
				+ queryParams);

		for (Status status : statuses) {
			ContentValues statusValues = StatusFactory.to(status);

			provider.insert(fullUri, statusValues);
			syncResult.stats.numEntries++;
			syncResult.stats.numInserts++;
		}

		// I don't want to be notified when
		// sync request is issued by user
		if (autosync)
			notifyNewStatus(statuses);
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param statuses
	 */
	protected void notifyNewStatus(List<Status> statuses) {
		if (statuses.size() > 0) {
			Log.d(TAG, "Send notification to user");
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					this.getContext()).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("Tatami")
					.setContentText(statuses.size() + " new statuses!")
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

	/**
	 * <p>
	 * Return the last status
	 * </p>
	 * 
	 * @param provider
	 * @return
	 * @throws RemoteException
	 */
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
