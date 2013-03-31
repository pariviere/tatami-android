package tatami.android.content;

import tatami.android.model.Status;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * <p>
 * Implementation of {@link AsyncTaskLoader} to {@link Status}es form database.
 * </p>
 * 
 * @author pariviere
 */
public class TimelineLoader extends AsyncTaskLoader<Cursor> {
	private final static String TAG = TimelineLoader.class.getSimpleName();

	public TimelineLoader(Context context) {
		super(context);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		Log.d(TAG, "Force loading of " + TAG);
		forceLoad();
	}

	@Override
	public Cursor loadInBackground() {
		DbHelper helper = DbHelper.getDbHelpder(getContext());
		return helper.findTimeline();
	}

}
