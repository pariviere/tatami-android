package tatami.android.fragment;

import tatami.android.R;
import tatami.android.widget.StatusObserver;
import tatami.android.widget.StatusesAdapter;
import android.database.Cursor;
import android.net.Uri;
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
 * </p>
 * 
 * @author pariviere
 */
public class StatusesList extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private final static String TAG = StatusesList.class.getSimpleName();

	private StatusesAdapter statusesAdapter = null;
	private PullToRefreshListView pullToRefreshListView = null;
	private StatusObserver observer = null;

	public StatusesAdapter getStatusesAdapter() {
		return statusesAdapter;
	}

	public PullToRefreshListView getPullToRefreshListView() {
		return this.pullToRefreshListView;
	}

	public void stopLoading() {
		Log.d(TAG, "Stop refresh animation");

		statusesAdapter.notifyDataSetChanged();
		if (pullToRefreshListView.isRefreshing()) {
			pullToRefreshListView.onRefreshComplete();
		}
		
		getLoaderManager().restartLoader(StatusesList.class.hashCode(), null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.d(TAG, "Statuses list fragment loading...");

		View view = inflater.inflate(R.layout.fragment_list_statuses,
				container, false);

		this.pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.status_list_view);
		

		observer = new StatusObserver(this);
		this.pullToRefreshListView.setOnRefreshListener(observer);

		getActivity().getContentResolver().registerContentObserver(
				Uri.parse("content://tatami.android.provider/status/"), false,
				observer);

		getLoaderManager()
				.initLoader(StatusesList.class.hashCode(), null, this);
		this.statusesAdapter = new StatusesAdapter(getActivity(), null);
		this.pullToRefreshListView.setAdapter(statusesAdapter);

		return view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "Loading cursor");
		return new CursorLoader(getActivity(),
				Uri.parse("content://tatami.android.provider/status/"), null,
				null, null, null);
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
