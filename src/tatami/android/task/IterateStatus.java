package tatami.android.task;

import tatami.android.TimelineActivity;
import android.os.AsyncTask;

public class IterateStatus
		extends
		AsyncTask<tatami.android.model.Status, tatami.android.model.Status, Void> {

	private final TimelineActivity timeline;

	public IterateStatus(TimelineActivity timeline) {
		this.timeline = timeline;
	}

	@Override
	protected Void doInBackground(final tatami.android.model.Status... statuses) {
		for (tatami.android.model.Status status : statuses) {
			publishProgress(status);
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(tatami.android.model.Status... statuses) {
		for (tatami.android.model.Status status : statuses) {
			timeline.getStatusesAdapter().add(status);
		}
	}
}