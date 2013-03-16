package tatami.android.widget;

import tatami.android.AsyncRequestHandler;
import tatami.android.TatamiApp;
import tatami.android.fragment.StatusesList;
import android.database.ContentObserver;
import android.net.Uri;
import android.util.Log;

import com.octo.android.robospice.SpiceManager;

public class StatusesObserver extends ContentObserver {
	private final static String TAG = StatusesObserver.class.getSimpleName();

	private SpiceManager spiceManager = new SpiceManager(
			AsyncRequestHandler.class);

	private StatusesList activity;
	private TatamiApp tatamiApp;

	public StatusesObserver(StatusesList activity) {
		super(null);
		this.activity = activity;
		this.tatamiApp = (TatamiApp) activity.getActivity()
				.getApplicationContext();
	}

	@Override
	public void onChange(boolean selfChange, Uri uri) {
		Log.d(TAG, "Detect change on " + uri.toString());

		activity.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				activity.stopLoading();
			}
		});
	}
}
