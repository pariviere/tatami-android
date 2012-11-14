package tatami.android.widget;

import java.util.AbstractMap.SimpleEntry;

import tatami.android.TimelineActivity;
import tatami.android.model.Status;
import tatami.android.task.GetTimeline;
import android.os.AsyncTask;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * <p>
 * {@link OnScrollListener} implementation for infinite {@link TimelineActivity}
 * scrolling
 * </p>
 * 
 * <p>
 * Loading is handled by {@link GetTimeline} {@link AsyncTask}
 * </p>
 * 
 * @author pariviere
 * 
 * @see http://benjii.me/2010/08/endless-scrolling-listview-in-android/
 */
public class TimelineScrollListener implements OnScrollListener {
	private int visibleThreshold = 5;
	private int previousTotal = 0;
	private boolean loading = true;
	private int currentPage = 0;

	private final TimelineActivity timeline;

	
	public void reset() {
		previousTotal = 0;
		loading = true;
		currentPage = 0;
	}
	
	/**
	 * <p>
	 * A reference to {@link TimelineActivity} is required.
	 * </p>
	 * 
	 * @param timeline
	 */
	public TimelineScrollListener(TimelineActivity timeline) {
		this.timeline = timeline;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (loading) {
			if (totalItemCount > previousTotal) {
				loading = false;
				previousTotal = totalItemCount;
				currentPage++;
			}
		}

		if (!loading
				&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

			int count = timeline.getStatusesAdapter().getCount();
			Status last = timeline.getStatusesAdapter().getItem(count - 1);
			String lastId = last.getStatusId();

			new GetTimeline(timeline).execute(new SimpleEntry<String, String>(
					"max_id", lastId));

			loading = true;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
