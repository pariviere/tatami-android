package tatami.android.fragment;

import tatami.android.R;
import tatami.android.content.UriBuilder;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import tatami.android.sync.SyncMeta;
import tatami.android.task.TriggerSync;
import tatami.android.widget.DetailsObserver;
import tatami.android.widget.StatusesAdapter;
import android.app.Activity;
import android.content.Intent;
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

public class DetailsStatus extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private final static String TAG = DetailsStatus.class.getSimpleName();

	private StatusesAdapter statusesAdapter = null;
	private PullToRefreshListView pullToRefreshListView = null;
	private Status currentStatus = null;
	private DetailsObserver detailsObserver;

	public StatusesAdapter getStatusesAdapter() {
		return statusesAdapter;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().getContentResolver().unregisterContentObserver(
				detailsObserver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		detailsObserver = new DetailsObserver(this);
		Log.d(TAG, "Details list fragment loading...");

		View view = inflater.inflate(R.layout.fragment_list_statuses,
				container, false);

		this.pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.status_list_view);

		this.statusesAdapter = new StatusesAdapter(getActivity(), null);
		this.pullToRefreshListView.setAdapter(statusesAdapter);

		Activity activity = this.getActivity();
		long id = activity.getIntent().getLongExtra("STATUSKEY", 0);

		Intent intent = new Intent(activity, TriggerSync.class);

		Log.d(TAG, "Selected status primary key is  " + id);

		Cursor cursor = activity.getContentResolver().query(
				UriBuilder.getStatusUri(id), null, null, null, null);

		if (cursor.getCount() != 0) {
			Status status = StatusFactory.fromCursorRow(cursor);
			currentStatus = status;
			String statusId = status.getStatusId();

			Log.d(TAG, "Current status is " + statusId);

			intent.putExtra(SyncMeta.TYPE, SyncMeta.TYPE_DETAILS);
			intent.putExtra(SyncMeta.STATUS_ID, statusId);

			activity.startService(intent);

			getActivity().getContentResolver().registerContentObserver(
					UriBuilder.getDetailsUri(statusId), false, detailsObserver);

		}

		cursor.close();

		getLoaderManager().initLoader(DetailsStatus.class.hashCode(), null,
				this);

		return view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "Loading cursor");
		if (currentStatus != null) {
			return new CursorLoader(getActivity(),
					UriBuilder.getDetailsUri(currentStatus.getStatusId()),
					null, null, null, null);
		} else {
			Log.w(TAG, "No status selected??");
			return null;
		}
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