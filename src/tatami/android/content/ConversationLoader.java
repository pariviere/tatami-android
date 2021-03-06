package tatami.android.content;

import tatami.android.model.Status;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * <p>
 * Implementation of {@link AsyncTaskLoader} to load conversation form database.
 * It requires a {@link Status} form which to load conversation.
 * </p>
 * 
 * @author pariviere
 */
public class ConversationLoader extends AsyncTaskLoader<Cursor> {
	private final static String TAG = ConversationLoader.class.getSimpleName();

	public Status forStatus;

	public ConversationLoader(Context context, Status forStatus) {
		super(context);
		this.forStatus = forStatus;
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
		return helper.findConversationForStatus(forStatus);
	}
}
