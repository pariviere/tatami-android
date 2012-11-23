package tatami.android.task;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import tatami.android.Constants;
import tatami.android.TimelineActivity;
import tatami.android.model.StatusFactory;
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
				tatami.android.model.Status status = StatusFactory
						.fromCursorRow(cursor);

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
