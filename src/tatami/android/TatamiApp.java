package tatami.android;

import java.util.Calendar;

import tatami.android.model.Status;
import tatami.android.task.TriggerSync;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * @author pariviere
 */
public class TatamiApp extends android.app.Application {

	@Override
	public void onCreate() {
		super.onCreate();
		scheduleTask();
	}

	protected void scheduleTask() {
		Calendar cal = Calendar.getInstance();

		Intent intent = new Intent(this, TriggerSync.class);
		PendingIntent pendingIntent = PendingIntent.getService(this,
				TatamiApp.class.hashCode(), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
	}

	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService((Context.CONNECTIVITY_SERVICE));
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiConn = networkInfo.isConnected();
		networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileConn = networkInfo.isConnected();

		return isWifiConn || isMobileConn;
	}
}
