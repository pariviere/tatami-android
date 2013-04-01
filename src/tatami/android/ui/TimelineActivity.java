package tatami.android.ui;

import java.util.HashMap;

import tatami.android.R;
import tatami.android.content.DbHelper;
import tatami.android.model.Status;
import tatami.android.request.PersistTimeline;
import tatami.android.request.PtrAwareTimelineListener;
import tatami.android.request.TimelineRequest;
import tatami.android.sync.SyncMeta;
import tatami.android.ui.widget.StatusAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.octo.android.robospice.persistence.DurationInMillis;

/**
 * <p>
 * 
 * </p>
 * 
 * @author pariviere
 */
public class TimelineActivity extends BaseFragmentActivity implements
		OnRefreshListener2<ListView> {
	private final static String TAG = TimelineActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle bundle) {
		Log.d(TAG, "Start timeline...");
		super.onCreate(bundle);
		this.setTitle(R.string.title_activity_timeline);
		setContentView(R.layout.activity_timeline);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_timeline, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_compose:
			ComposeDialog dialog = new ComposeDialog();
			dialog.show(getSupportFragmentManager(), "TimelineActivity");
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public void onStart() {
		super.onStart();

		TimelineRequest request = new TimelineRequest(this,
				new HashMap<String, String>());
		PersistTimeline listener = new PersistTimeline(this);

		spiceManager.execute(request, request.toString(),
				DurationInMillis.ONE_MINUTE, listener);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		HashMap<String, String> extra = new HashMap<String, String>();

		TimelineRequest request = new TimelineRequest(this, extra);
		PersistTimeline listener = new PtrAwareTimelineListener(this,
				refreshView);
		spiceManager.execute(request, request.toString(),
				DurationInMillis.ONE_MINUTE, listener);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) refreshView
				.getRefreshableView().getAdapter();
		StatusAdapter statusesAdapter = (StatusAdapter) headerViewListAdapter
				.getWrappedAdapter();

		long id = statusesAdapter.getItemId(statusesAdapter.getCount() - 1);

		Log.d(TAG, "Last status primary key is  " + id);

		DbHelper helper = DbHelper.getDbHelpder(this);
		Status status = helper.getStatus(id);

		if (status != null) {
			String statusId = status.getStatusId();

			HashMap<String, String> extra = new HashMap<String, String>();
			extra.put(SyncMeta.BEFORE_ID, statusId);

			TimelineRequest request = new TimelineRequest(this, extra);
			PersistTimeline listener = new PtrAwareTimelineListener(this,
					refreshView);

			spiceManager.execute(request, request.toString(),
					DurationInMillis.ONE_MINUTE, listener);
		} else {
			refreshView.onRefreshComplete();
		}

	}

}
