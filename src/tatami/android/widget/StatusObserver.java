package tatami.android.widget;

import tatami.android.R;
import tatami.android.fragment.StatusesList;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import tatami.android.sync.SyncMeta;
import tatami.android.task.TriggerSync;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class StatusObserver extends ContentObserver implements
		OnRefreshListener2<ListView> {
	private final static String TAG = StatusObserver.class.getSimpleName();

	private StatusesList activity;

	public StatusObserver(StatusesList activity) {
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

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// Log.d(TAG, "onPullDownToRefresh");
		//
		// PullToRefreshListView listView = (PullToRefreshListView) refreshView
		// .findViewById(R.id.status_list_view);
		//
		// listView.setLastUpdatedLabel(DateUtils.formatDateTime(
		// activity.getActivity(), System.currentTimeMillis(),
		// DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
		// | DateUtils.FORMAT_ABBREV_ALL));
		//
		// Intent intent = new Intent(activity.getActivity(),
		// TriggerSync.class);
		//
		// intent.putExtra("nothing", "");
		//
		// activity.getActivity().startService(intent);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		Log.d(TAG, "onPullUpToRefresh");

		PullToRefreshListView listView = (PullToRefreshListView) refreshView
				.findViewById(R.id.status_list_view);

		listView.setLastUpdatedLabel(DateUtils.formatDateTime(
				activity.getActivity(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL));

		Intent intent = new Intent(activity.getActivity(), TriggerSync.class);

		StatusesAdapter statusesAdapter = activity.getStatusesAdapter();

		long id = statusesAdapter.getItemId(statusesAdapter.getCount() - 1);

		Log.d(TAG, "Last status primary key is  " + id);

		Cursor cursor = activity
				.getActivity()
				.getContentResolver()
				.query(Uri.parse("content://tatami.android.provider/status/"
						+ id), null, null, null, null);

		if (cursor.getCount() != 0) {
			Status status = StatusFactory.fromCursorRow(cursor);
			String statusId = status.getStatusId();
			intent.putExtra(SyncMeta.BEFORE_ID, statusId);

			activity.getActivity().startService(intent);
		} else {
			listView.onRefreshComplete();
		}
	}
}
