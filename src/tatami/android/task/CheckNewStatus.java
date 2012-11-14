package tatami.android.task;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import tatami.android.Application;
import tatami.android.Client;
import tatami.android.R;
import tatami.android.TimelineActivity;
import tatami.android.model.Status;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;

public class CheckNewStatus extends IntentService {
	private int id = CheckNewStatus.class.hashCode();

	public CheckNewStatus() {
		super(CheckNewStatus.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Application app = (Application) getApplication();

		if (app.isConnected()) {

			Status lastSeen = app.getLastSeen();
			if (lastSeen != null) {

				try {
					SimpleEntry<String, String> sinceId = new SimpleEntry<String, String>(
							"since_id", lastSeen.getStatusId());
					List<Status> statuses = Client.getInstance().getTimeline(
							sinceId);

					notifyNewStatus(statuses);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}
	}

	protected void notifyNewStatus(List<Status> statuses) {
		if (statuses.size() > 0) {

			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					this).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("Tatami").setContentText("New statuses")
					.setNumber(statuses.size()).setAutoCancel(true);

			Intent resultIntent = new Intent(this, TimelineActivity.class);

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(TimelineActivity.class);
			stackBuilder.addNextIntent(resultIntent);

			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

			inboxStyle.setBigContentTitle("Details");

			for (Status status : statuses) {

				int last = status.getContent().length();

				if (last > 30) {
					last = 30;
				}

				inboxStyle.addLine(status.getUsername() + ": "
						+ status.getContent().substring(0, last));
			}

			builder.setStyle(inboxStyle);

			builder.setContentIntent(resultPendingIntent);

			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(id, builder.build());
		}

	}

	/**
	 * 
	 * @param intent
	 * @param statuses
	 * @throws Exception
	 */
	protected void notifyActivity(Intent intent, List<Status> statuses)
			throws Exception {
		Bundle bundle = intent.getExtras();

		if (bundle != null) {
			Messenger messenger = (Messenger) intent.getExtras().get(
					"tatami.android.timelineMessenger");
			Message message = Message.obtain();
			message.arg1 = statuses.size();

			messenger.send(message);
		}
	}
}
