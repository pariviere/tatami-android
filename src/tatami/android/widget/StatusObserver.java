package tatami.android.widget;

import tatami.android.TimelineActivity;
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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class StatusObserver extends ContentObserver implements
		OnRefreshListener {
	
	
	private boolean loading = false;
	
	private final static String TAG = StatusObserver.class.getSimpleName();

	private TimelineActivity activity;

	public StatusObserver(TimelineActivity activity) {
		super(null);
		this.activity = activity;
	}

	@Override
	public void onChange(boolean selfChange, Uri uri) {
		Log.d(TAG, "Detect change on " + uri.toString());

		
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Log.d(TAG, "OnUI Thread");
				activity.stopLoading();
			}
		});
	}

	@Override
	public void onRefresh(PullToRefreshBase refreshView) {
		
		
		Log.d(TAG, "OnRefreshListener");

		PullToRefreshListView listView = activity.getPullToRefreshListView();
		
		listView.setLastUpdatedLabel(DateUtils.formatDateTime(
				activity, System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL));

		Intent intent = new Intent(activity, TriggerSync.class);

		StatusesAdapter statusesAdapter = activity.getStatusesAdapter();
		
		long id = statusesAdapter.getItemId(statusesAdapter.getCount() - 1);

		Log.d(TAG, "Last status primary key is  " + id);

		Cursor cursor = activity.getContentResolver().query(
				Uri.parse("content://tatami.android.provider/status/" + id),
				null, null, null, null);

		Status status = StatusFactory.fromCursorRow(cursor);
		String statusId = status.getStatusId();
		intent.putExtra(SyncMeta.BEFORE_ID, statusId);

		activity.startService(intent);
	}

}
