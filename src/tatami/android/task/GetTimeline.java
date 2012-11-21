package tatami.android.task;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tatami.android.Constants;
import tatami.android.TimelineActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

/**
 * <p>
 * </p>
 * 
 * @author pariviere
 */
public class GetTimeline
		extends
		AsyncTask<SimpleEntry<String, String>, Void, List<tatami.android.model.Status>> {
	private final TimelineActivity timeline;

	public GetTimeline(TimelineActivity timeline) {
		this.timeline = timeline;
	}

	@Override
	protected void onPreExecute() {
		timeline.notifyLoading();
	}

	@Override
	protected List<tatami.android.model.Status> doInBackground(
			SimpleEntry<String, String>... params) {

		try {

			Uri.Builder fullUri = new Uri.Builder();
			fullUri.scheme("content");
			fullUri.authority(Constants.AUTHORITY);
			fullUri.appendPath("status");

			Cursor cursor = timeline.getContentResolver().query(
					fullUri.build(), null, null, null, null);

			List<tatami.android.model.Status> statuses = new ArrayList<tatami.android.model.Status>();

			while (cursor.moveToNext()) {
				String statusId = cursor.getString(cursor
						.getColumnIndex("statusId"));
				String username = cursor.getString(cursor
						.getColumnIndex("username"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				String gravatar = cursor.getString(cursor
						.getColumnIndex("gravatar"));

				tatami.android.model.Status status = new tatami.android.model.Status();
				status.setStatusId(statusId);
				status.setContent(content);
				status.setUsername(username);
				status.setStatusDate(new Date());
				status.setGravatar(gravatar);
				
				statuses.add(status);

			}

			return statuses;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ArrayList<tatami.android.model.Status>();
		}
	}

	@Override
	protected void onPostExecute(List<tatami.android.model.Status> statuses) {
		timeline.populateListView(statuses);
		timeline.notifyNoLoading();
	}
}
