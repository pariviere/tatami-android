package tatami.android.task;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import tatami.android.Client;
import tatami.android.TimelineActivity;
import android.content.Intent;
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

			List<tatami.android.model.Status> statuses = Client.getInstance()
					.getTimeline(params);

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
