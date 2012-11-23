package tatami.android.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * <p>
 * Entry point for {@link SyncAdapter} : Android will start this service,
 * retrieve the binded {@link SyncAdapter} and perform sync
 * </p>
 * 
 * @author pariviere
 */
public class SyncService extends Service {
	private static String TAG = SyncService.class.getSimpleName();

	private static final Object syncAdapterLock = new Object();
	private SyncAdapter syncAdapter;

	public SyncService() {
		super();

		Log.d(TAG, "SyncService started");

		synchronized (syncAdapterLock) {
			if (syncAdapter == null) {
				syncAdapter = new SyncAdapter(this, true);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}

}
