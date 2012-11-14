package tatami.android;

import java.util.List;

import tatami.android.model.Status;
import tatami.android.task.CheckNewStatus;
import tatami.android.task.GetTimeline;
import tatami.android.task.IterateStatus;
import tatami.android.widget.TimelineScrollListener;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * <p>
 * 
 * </p>
 * 
 * @author pariviere
 */
public class TimelineActivity extends ListActivity {
	private StatusesAdapter statusesAdapter = null;
	private ListView listView = null;
	private MenuItem refreshMenuItem;

	private Handler newStatusHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			Toast.makeText(TimelineActivity.this, "Received " + msg.arg1,
					Toast.LENGTH_SHORT).show();
		}
	};

	public StatusesAdapter getStatusesAdapter() {
		return statusesAdapter;
	}

	public void doReload(View view) {

		Application app = (Application) getApplication();

		if (!app.isConnected()) {
			Toast.makeText(this, "No network found", Toast.LENGTH_SHORT).show();
		} else {
			this.listView.setOnScrollListener(null);
			this.statusesAdapter.clear();
			this.listView.setOnScrollListener(new TimelineScrollListener(this));
			new GetTimeline(this).execute();

			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(CheckNewStatus.class.hashCode());
		}
	}

	/**
	 * <p>
	 * </p>
	 */
	public void populateListView(List<tatami.android.model.Status> statuses) {

		if (statuses.size() > 0) {
			Status lastSeen = statuses.get(0);

			Application app = (Application) getApplication();
			app.setLastSeen(lastSeen);
		}
		new IterateStatus(this).execute(statuses.toArray(new Status[] {}));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);

		this.listView = getListView();
		this.statusesAdapter = new StatusesAdapter(this);
		this.listView.setAdapter(statusesAdapter);

		Intent intent = new Intent(this, CheckNewStatus.class);
		Messenger messenger = new Messenger(newStatusHandler);
		intent.putExtra("tatami.android.timelineMessenger", messenger);
		// this.startService(intent);

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
