package tatami.android;

import java.util.List;

import tatami.android.content.UriBuilder;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import tatami.android.widget.StatusesAdapter;
import tatami.android.widget.StatusesAdapter.ViewHolder;
import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailsActivity extends Activity {
	private final static String TAG = DetailsActivity.class.getSimpleName();

	RelativeLayout detailsLayout;
	ImageView avatar;
	TextView status;
	TextView info;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		avatar = (ImageView) findViewById(R.id.avatar);
		status = (TextView) findViewById(R.id.status);
		info = (TextView) findViewById(R.id.info);

		detailsLayout = (RelativeLayout) findViewById(R.id.details_status);
	}

	@Override
	protected void onResume() {
		super.onResume();

		long id = getIntent().getLongExtra("STATUSKEY", 0);

		Cursor cursor = getContentResolver().query(UriBuilder.getStatusUri(id),
				null, null, null, null);
		
		Status instance = StatusFactory.fromCursorRow(cursor);
		
		try {
			// Only to mimic CursorAdapter behavior and
			// to reuse StatusesAdapter code in this case
			// where there's no ListView
			StatusesAdapter adapter = new StatusesAdapter(this, cursor);

			View rowView = detailsLayout;
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.avatar = avatar;
			viewHolder.status = status;
			viewHolder.info = info;

			rowView.setTag(viewHolder);

			adapter.bindView(rowView, this, cursor);
		} finally {
			cursor.close();
		}

		new AsyncTask<String, Void, Void>() {
			protected Void doInBackground(String[] params) {
				try {
					String statusId = params[0];
					List<tatami.android.model.Status> statuses = Client
							.getInstance().getDiscussion(statusId);
					Log.d(TAG, "Found " + statuses.size() + " replies");

				} catch (Exception ex) {
					Log.e(TAG, ex.getMessage(), ex);
				}
				return null;
			}
		}.execute(instance.getStatusId());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
