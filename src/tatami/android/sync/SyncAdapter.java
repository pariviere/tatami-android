package tatami.android.sync;

import java.util.HashMap;

import tatami.android.R;
import tatami.android.model.Status;
import tatami.android.request.ListStatus;
import tatami.android.request.TimelineListener;
import tatami.android.request.TimelineRequest;
import tatami.android.ui.TimelineActivity;
import android.accounts.Account;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * <p>
 * Implementation of Android {@link AbstractThreadedSyncAdapter} API in order to
 * bring synchronization to Tatami.
 * </p>
 * 
 * <p>
 * NOTE : no need to test if network is available. Android handles it for us.
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
		doSyncTimeline(extras, provider, syncResult);
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
			SyncResult syncResult) {

		// Robspice request and listener are used but without
		// using robospice async feature.
		// Indeed, SyncAdapter already used a threading approach.
		Context context = this.getContext();
		TimelineRequest request = new TimelineRequest(context,
				new HashMap<String, String>());
		TimelineListener listener = new TimelineListener(context);

		try {
			ListStatus listStatus = request.loadDataFromNetwork();
			listener.onRequestSuccess(listStatus);
			notifyNewStatus(listStatus);
			syncResult.stats.numUpdates++;
		} catch (Exception ex) {
			syncResult.stats.numIoExceptions++;
			listener.onRequestFailure(new SpiceException(ex));
		}

	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param statuses
	 */
	protected void notifyNewStatus(ListStatus statuses) {
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
}
