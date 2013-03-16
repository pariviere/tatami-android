package tatami.android;

import java.util.Calendar;

import tatami.android.request.ConversationDetails;
import tatami.android.request.ListStatus;
import tatami.android.sync.TriggerSync;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.util.LruCache;

import com.octo.android.robospice.persistence.memory.CacheItem;

/**
 * <p>
 * Implementation of {@link Application} in order to handle common behaviors
 * </p>
 * 
 * @author pariviere
 */
public class AppState extends Application {
	public static LruCache<Object, CacheItem<ListStatus>> listStatus = new LruCache<Object, CacheItem<ListStatus>>(
			4 * 1024 * 1024);

	public static LruCache<Object, CacheItem<ConversationDetails>> conversationCache = new LruCache<Object, CacheItem<ConversationDetails>>(
			4 * 1024 * 1024);

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
				AppState.class.hashCode(), intent,
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
		boolean isConnected = false;
		if (activeNetwork != null) {
			isConnected = activeNetwork.isConnectedOrConnecting();
		}

		return isConnected;
	}
}
