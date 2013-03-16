package tatami.android.ui.widget;

import tatami.android.ui.fragment.StatusesList;
import android.database.ContentObserver;
import android.net.Uri;
import android.util.Log;

public class StatusesObserver extends ContentObserver {
	private final static String TAG = StatusesObserver.class.getSimpleName();

	private StatusesList activity;

	public StatusesObserver(StatusesList activity) {
		super(null);
		this.activity = activity;
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
