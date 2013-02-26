package tatami.android;

import java.util.Calendar;

import tatami.android.task.TriggerSync;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <p>
 * Implementation of {@link Application} in order to handle common behaviors
 * </p>
 * 
 * @author pariviere
 */
public class TatamiApp extends android.app.Application {

	@Override
	public void onCreate() {
		super.onCreate();
		doScheduleTask();
	}

	/**
	 * <p>
	 * Schedule {@link TriggerSync}
	 * </p>
	 */
	protected void doScheduleTask() {
		Calendar cal = Calendar.getInstance();

		Intent intent = new Intent(this, TriggerSync.class);
		PendingIntent pendingIntent = PendingIntent.getService(this,
				TatamiApp.class.hashCode(), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
	}

	/**
	 * <p>
	 * Whether the device is connected or not
	 * </p>
	 * 
	 * @return
	 */
	public boolean isConnected() {
		// Paste from android developer guide
		// http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork.isConnectedOrConnecting();

		return isConnected;
	}
}
