package tatami.android;

import tatami.android.widget.StatusObserver;
import tatami.android.widget.StatusesAdapter;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * <p>
 * 
 * </p>
 * 
 * @author pariviere
 */
public class TimelineActivity extends ListActivity {
	private final static String TAG = TimelineActivity.class.getSimpleName();
	private StatusesAdapter statusesAdapter = null;
	private PullToRefreshListView pullToRefreshListView = null;
	private MenuItem refreshMenuItem;
	private StatusObserver observer = null;

	public StatusesAdapter getStatusesAdapter() {
		return statusesAdapter;
	}

	public PullToRefreshListView getPullToRefreshListView() {
		return this.pullToRefreshListView;
	}

	public void stopLoading() {
		Log.d(TAG, "Stop refresh animation");

		Cursor cursor = managedQuery(
				Uri.parse("content://tatami.android.provider/status/"), null,
				null, null, null);
		statusesAdapter.swapCursor(cursor);
			
		statusesAdapter.notifyDataSetChanged();
		if (pullToRefreshListView.isRefreshing()) {
			pullToRefreshListView.onRefreshComplete();

			pullToRefreshListView.refreshDrawableState();

			pullToRefreshListView.getRefreshableView().refreshDrawableState();
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);

		this.pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.status_list_view);

		observer = new StatusObserver(this);

		this.pullToRefreshListView.setOnRefreshListener(observer);

		this.getContentResolver().registerContentObserver(
				Uri.parse("content://tatami.android.provider/status/"), false,
				observer);

		Cursor cursor = managedQuery(
				Uri.parse("content://tatami.android.provider/status/"), null,
				null, null, null);

		this.statusesAdapter = new StatusesAdapter(this, cursor);
		this.pullToRefreshListView.setAdapter(statusesAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// doReload(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_timeline, menu);

		refreshMenuItem = menu.findItem(R.id.menu_refresh);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;

		case R.id.menu_refresh:
			// doReload(null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void notifyLoading() {
		if (refreshMenuItem != null) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ImageView iv = (ImageView) inflater.inflate(
					R.layout.refresh_action_view, null);

			Animation rotation = AnimationUtils.loadAnimation(this,
					R.animator.clockwise_refresh);
			rotation.setRepeatCount(Animation.INFINITE);
			iv.startAnimation(rotation);

			refreshMenuItem.setActionView(iv);
		}
	}

	public void notifyNoLoading() {
		if (refreshMenuItem != null && refreshMenuItem.getActionView() != null) {
			refreshMenuItem.getActionView().clearAnimation();
			refreshMenuItem.setActionView(null);
		}
	}

}
