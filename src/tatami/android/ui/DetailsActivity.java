package tatami.android.ui;

import tatami.android.Constants;
import tatami.android.R;
import tatami.android.content.DbHelper;
import tatami.android.model.Status;
import tatami.android.request.ConversationDetailsRequest;
import tatami.android.request.PersistConversation;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.octo.android.robospice.persistence.DurationInMillis;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * <p>
 * </p>
 * 
 * @author pariviere
 */
public class DetailsActivity extends BaseFragmentActivity {
	private final static String TAG = DetailsActivity.class.getSimpleName();

	private Status forStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		long id = this.getIntent().getLongExtra(Constants.STATUS_PARAM, 0);

		DbHelper helper = DbHelper.getDbHelpder(this);
		forStatus = helper.getStatus(id);

		if (forStatus == null) {
			String msg = TAG + " requires a specific status.";
			Crouton.makeText(this, msg, Style.ALERT);
			Log.e(TAG, msg);
		} else {
			Log.d(TAG, "Launch details for status : " + forStatus.toString());
			String title = String
					.format("@%s says...", forStatus.getUsername());
			setTitle(title);
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
			finish();

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
		super.onStart();

		ConversationDetailsRequest request = new ConversationDetailsRequest(
				this, forStatus.getStatusId());
		PersistConversation listener = new PersistConversation(this);

		spiceManager.execute(request, request.toString(),
				DurationInMillis.ONE_MINUTE * 2, listener);
	}

}
