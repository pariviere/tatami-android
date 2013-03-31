package tatami.android.ui.fragment;

import tatami.android.Constants;
import tatami.android.R;
import tatami.android.content.ConversationLoader;
import tatami.android.content.DbHelper;
import tatami.android.events.PersistConversationDone;
import tatami.android.model.Status;
import tatami.android.ui.widget.StatusesAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * <p>
 * This implementation of {@link ListFragment} display a list of {@link Status}
 * linked to a {@link Status}.
 * </p>
 * 
 * @author pariviere
 */
public class ConversationDetails extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private final static String TAG = ConversationDetails.class.getSimpleName();

	private StatusesAdapter statusesAdapter = null;
	private PullToRefreshListView pullToRefreshListView = null;
	private Status forStatus = null;

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "Loading cursor");
		if (forStatus != null) {
			Loader<Cursor> cursorLoader = new ConversationLoader(getActivity(),
					forStatus);
			return cursorLoader;
		} else {
			Log.e(TAG, "No status selected? How can I display a conversation?");
			return null;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_details_status,
				container, false);

		this.pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.status_list_view);
		this.statusesAdapter = new StatusesAdapter(getActivity(), null);
		this.pullToRefreshListView.setAdapter(statusesAdapter);

		Activity activity = this.getActivity();
		long id = activity.getIntent().getLongExtra(Constants.STATUS_PARAM, 0);
		Log.d(TAG, "Selected status primary key is  " + id);

		DbHelper helper = DbHelper.getDbHelpder(getActivity());
		forStatus = helper.getStatus(id);

		if (forStatus == null) {
			String msg = TAG + " requires a specific status.";
			Crouton.makeText(getActivity(), msg, Style.ALERT);
			Log.e(TAG, msg);
		}

		getLoaderManager().initLoader(ConversationDetails.class.hashCode(),
				null, this);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(PersistConversationDone done) {
		getLoaderManager().restartLoader(ConversationDetails.class.hashCode(),
				null, this);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		statusesAdapter.swapCursor(null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		statusesAdapter.swapCursor(cursor);
	}
}