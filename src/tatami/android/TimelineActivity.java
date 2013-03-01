package tatami.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * <p>
 * 
 * </p>
 * 
 * @author pariviere
 */
public class TimelineActivity extends FragmentActivity {
	private final static String TAG = TimelineActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle bundle) {
		Log.d(TAG, "Start timeline...");
		super.onCreate(bundle);
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

}
