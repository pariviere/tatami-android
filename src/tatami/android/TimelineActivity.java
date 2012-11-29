package tatami.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

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
}
