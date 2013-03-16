package tatami.android;

import java.util.HashMap;

import tatami.android.content.UriBuilder;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import tatami.android.request.TimelineListener;
import tatami.android.request.TimelineRequest;
import tatami.android.sync.SyncMeta;
import tatami.android.widget.StatusesAdapter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;

/**
 * <p>
 * 
 * </p>
 * 
 * @author pariviere
 */
public class TimelineActivity extends FragmentActivity implements
		OnRefreshListener2<ListView> {
	private final static String TAG = TimelineActivity.class.getSimpleName();
	private SpiceManager spiceManager = new SpiceManager(
			AsyncRequestHandler.class);

	@Override
	protected void onCreate(Bundle bundle) {
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
		spiceManager.start(this);
		super.onStart();

		TimelineRequest request = new TimelineRequest(this,
				new HashMap<String, String>());
		TimelineListener listener = new TimelineListener(this);

		spiceManager.execute(request, request.toString(),
				DurationInMillis.ONE_MINUTE, listener);
	}

	@Override
	public void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		PullToRefreshListView listView = (PullToRefreshListView) refreshView
				.findViewById(R.id.status_list_view);

		HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listView
				.getRefreshableView().getAdapter();
		StatusesAdapter statusesAdapter = (StatusesAdapter) headerViewListAdapter
				.getWrappedAdapter();

		long id = statusesAdapter.getItemId(statusesAdapter.getCount() - 1);

		Log.d(TAG, "Last status primary key is  " + id);

		Cursor cursor = getContentResolver().query(UriBuilder.getStatusUri(id),
				null, null, null, null);

		if (cursor.getCount() != 0) {
			Status status = StatusFactory.fromCursorRow(cursor);
			String statusId = status.getStatusId();

			HashMap<String, String> extra = new HashMap<String, String>();
			extra.put(SyncMeta.BEFORE_ID, statusId);

			TimelineRequest request = new TimelineRequest(this, extra);
			TimelineListener listener = new TimelineListener(this);

			spiceManager.execute(request, request.toString(),
					DurationInMillis.ONE_MINUTE, listener);
		} else {
			listView.onRefreshComplete();
		}

	}

}
