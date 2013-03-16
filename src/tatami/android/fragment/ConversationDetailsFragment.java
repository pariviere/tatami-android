package tatami.android.fragment;

import tatami.android.Constants;
import tatami.android.R;
import tatami.android.content.UriBuilder;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import tatami.android.widget.DetailsObserver;
import tatami.android.widget.StatusesAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * <p>
 * This implementation of {@link ListFragment} display a list of {@link Status}
 * linked to a {@link Status}.
 * </p>
 * 
 * @author pariviere
 */
public class ConversationDetailsFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private final static String TAG = ConversationDetailsFragment.class
			.getSimpleName();

	private StatusesAdapter statusesAdapter = null;
	private PullToRefreshListView pullToRefreshListView = null;
	private Status currentStatus = null;
	private DetailsObserver detailsObserver;

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "Loading cursor");
		if (currentStatus != null) {
			return new CursorLoader(getActivity(),
					UriBuilder.getDetailsUri(currentStatus.getStatusId()),
					null, null, null, null);
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

		this.detailsObserver = new DetailsObserver(this);

		Activity activity = this.getActivity();
		long id = activity.getIntent().getLongExtra(Constants.STATUS_PARAM, 0);
		Log.d(TAG, "Selected status primary key is  " + id);

		Cursor cursor = activity.getContentResolver().query(
				UriBuilder.getStatusUri(id), null, null, null, null);

		if (cursor.getCount() != 0) {
			Status status = StatusFactory.fromCursorRow(cursor);
			String forStatusId = status.getStatusId();
			currentStatus = status;
			getActivity().getContentResolver().registerContentObserver(
					UriBuilder.getDetailsUri(forStatusId), false,
					detailsObserver);
		}
		cursor.close();

		getLoaderManager().initLoader(
				ConversationDetailsFragment.class.hashCode(), null, this);

		return view;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		statusesAdapter.swapCursor(null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		statusesAdapter.swapCursor(cursor);
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().getContentResolver().unregisterContentObserver(
				detailsObserver);
	}
}