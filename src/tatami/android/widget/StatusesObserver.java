package tatami.android.widget;

import tatami.android.R;
import tatami.android.TatamiApp;
import tatami.android.content.UriBuilder;
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
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class StatusesObserver extends ContentObserver implements
		OnRefreshListener2<ListView> {
	private final static String TAG = StatusesObserver.class.getSimpleName();

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

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// / not yet implement
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

		PullToRefreshListView listView = (PullToRefreshListView) refreshView
				.findViewById(R.id.status_list_view);

		if (!tatamiApp.isConnected()) {
			Toast.makeText(activity.getActivity(), "No network",
					Toast.LENGTH_LONG).show();
			listView.onRefreshComplete();
		} else {
			listView.setLastUpdatedLabel(DateUtils.formatDateTime(
					activity.getActivity(), System.currentTimeMillis(),
					DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL));

			Intent intent = new Intent(activity.getActivity(),
					TriggerSync.class);
			StatusesAdapter statusesAdapter = activity.getStatusesAdapter();

			long id = statusesAdapter.getItemId(statusesAdapter.getCount() - 1);

			Log.d(TAG, "Last status primary key is  " + id);

			Cursor cursor = activity.getActivity().getContentResolver()
					.query(UriBuilder.getStatusUri(id), null, null, null, null);

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
}
