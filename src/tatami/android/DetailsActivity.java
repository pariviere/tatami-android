package tatami.android;

import tatami.android.content.UriBuilder;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class DetailsActivity extends FragmentActivity {
	private final static String TAG = DetailsActivity.class.getSimpleName();

	private Status currentStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		long id = this.getIntent().getLongExtra("STATUSKEY", 0);

		Cursor cursor = this.getContentResolver().query(
				UriBuilder.getStatusUri(id), null, null, null, null);

		if (cursor.getCount() != 0) {
			Status status = StatusFactory.fromCursorRow(cursor);
			currentStatus = status;
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
			args.putString("STATUSKEY", this.currentStatus.getStatusId());
			dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(), "DetailsActivity");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
