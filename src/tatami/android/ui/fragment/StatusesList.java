package tatami.android.ui.fragment;

import tatami.android.Constants;
import tatami.android.R;
import tatami.android.content.StatusLoader;
import tatami.android.events.PersistTimelineDone;
import tatami.android.ui.DetailsActivity;
import tatami.android.ui.TimelineActivity;
import tatami.android.ui.widget.StatusesAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import de.greenrobot.event.EventBus;

/**
 * <p>
 * </p>
 * 
 * @author pariviere
 */
public class StatusesList extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private final static String TAG = StatusesList.class.getSimpleName();

	private StatusesAdapter statusesAdapter = null;
	private PullToRefreshListView pullToRefreshListView = null;

	public StatusesAdapter getStatusesAdapter() {
		return statusesAdapter;
	}

	public PullToRefreshListView getPullToRefreshListView() {
		return this.pullToRefreshListView;
	}

	public void stopLoading() {
		getLoaderManager().restartLoader(StatusesList.class.hashCode(), null,
				this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(StatusesList.this.getActivity()
				.getApplicationContext(), DetailsActivity.class);
		intent.putExtra(Constants.STATUS_PARAM, id);
		startActivity(intent);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		int position = pullToRefreshListView.getRefreshableView()
				.getFirstVisiblePosition();

		outState.putInt("position", position);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);

		if (savedInstanceState != null
				&& savedInstanceState.containsKey("position")) {
			int position = savedInstanceState.getInt("position");

			pullToRefreshListView.getRefreshableView().setSelection(position);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.d(TAG, "Statuses list fragment loading...");

		View view = inflater.inflate(R.layout.fragment_list_statuses,
				container, false);

		this.pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.status_list_view);

		this.pullToRefreshListView.getRefreshableView().setItemsCanFocus(true);

		this.pullToRefreshListView
				.setOnRefreshListener((TimelineActivity) getActivity());

		getLoaderManager()
				.initLoader(StatusesList.class.hashCode(), null, this);

		this.statusesAdapter = new StatusesAdapter(getActivity(), null);
		this.pullToRefreshListView.setAdapter(statusesAdapter);

		return view;
	}

	public void onEventMainThread(PersistTimelineDone done) {
		stopLoading();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Loader<Cursor> cursorLoader = new StatusLoader(getActivity());

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		statusesAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		statusesAdapter.swapCursor(null);
	}
}
