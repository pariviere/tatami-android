package tatami.android;

import java.util.List;

import tatami.android.model.Status;
import tatami.android.sync.SyncMeta;
import tatami.android.task.GetTimeline;
import tatami.android.task.IterateStatus;
import tatami.android.task.TriggerSync;
import tatami.android.widget.StatusesAdapter;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * <p>
 * 
 * </p>
 * 
 * @author pariviere
 */
public class TimelineActivity extends ListActivity {
	private StatusesAdapter statusesAdapter = null;
	private PullToRefreshListView listView = null;
	private MenuItem refreshMenuItem;

	public StatusesAdapter getStatusesAdapter() {
		return statusesAdapter;
	}

	public void doReload(View view) {

		TatamiApp app = (TatamiApp) getApplication();

//		this.statusesAdapter.clear();
//		this.statusesAdapter.notifyDataSetChanged();
		new GetTimeline(this).execute();
	}

	/**
	 * <p>
	 * </p>
	 */
	public void populateListView(List<tatami.android.model.Status> statuses) {
		new IterateStatus(this).execute(statuses.toArray(new Status[] {}));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);

		this.listView = (PullToRefreshListView) findViewById(R.id.status_list_view);

		this.listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				listView.setLastUpdatedLabel(DateUtils.formatDateTime(
						getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));

				Intent intent = new Intent(TimelineActivity.this,
						TriggerSync.class);

				intent.putExtra(SyncMeta.BEFORE_ID,
						statusesAdapter.getItem(statusesAdapter.getCount() - 1)
								.getStatusId());

				startService(intent);
			}
		});

		this.statusesAdapter = new StatusesAdapter(this);
		this.listView.setAdapter(statusesAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		doReload(null);
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
			doReload(null);
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
