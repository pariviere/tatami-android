package tatami.android.task;

import tatami.android.TatamiApp;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * 
 * @author pariviere
 * 
 */
public class ComposeSync extends IntentService {
	private final static String TAG = ComposeSync.class.getSimpleName();

	public ComposeSync() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		TatamiApp app = (TatamiApp) getApplication();

		if (app.isConnected()) {

		} else {
			IntentFilter filter = new IntentFilter(
					ConnectivityManager.CONNECTIVITY_ACTION);
			app.registerReceiver(new NetworkChangeReceiver(), filter);
		}
	}

	private class NetworkChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			TatamiApp app = (TatamiApp) getApplication();

		}
	}

}
