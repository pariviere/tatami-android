package tatami.android;

import tatami.android.content.UriBuilder;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import tatami.android.request.ConversationDetailsListener;
import tatami.android.request.ConversationDetailsRequest;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;

/**
 * <p>
 * </p>
 * 
 * @author pariviere
 */
public class DetailsActivity extends FragmentActivity {
	private final static String TAG = DetailsActivity.class.getSimpleName();
	private SpiceManager spiceManager = new SpiceManager(
			AsyncRequestHandler.class);

	private Status forStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		long id = this.getIntent().getLongExtra(Constants.STATUS_PARAM, 0);

		Cursor cursor = this.getContentResolver().query(
				UriBuilder.getStatusUri(id), null, null, null, null);

		if (cursor.getCount() != 0) {
			Status status = StatusFactory.fromCursorRow(cursor);
			forStatus = status;
			Log.d(TAG, "Launch details for status : " + forStatus.toString());
		} else {
			Toast.makeText(this, "No status found?!", Toast.LENGTH_LONG).show();
			this.finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_timeline, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_compose:
			ComposeDialog dialog = new ComposeDialog();
			Bundle args = new Bundle();
			args.putString(Constants.STATUS_PARAM, this.forStatus.getStatusId());
			dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(), TAG);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		spiceManager.start(this);
		super.onStart();

		ConversationDetailsRequest request = new ConversationDetailsRequest(
				this, forStatus.getStatusId());
		ConversationDetailsListener listener = new ConversationDetailsListener(
				this);

		spiceManager.execute(request, request.toString(),
				DurationInMillis.ONE_MINUTE * 2, listener);
	}

	@Override
	public void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}

}
