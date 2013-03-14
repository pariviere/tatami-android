package tatami.android;

import java.util.HashMap;

import tatami.android.request.TimelineListener;
import tatami.android.request.TimelineRequest;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;

/**
 * <p>
 * 
 * </p>
 * 
 * @author pariviere
 */
public class TimelineActivity extends FragmentActivity {
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

}
