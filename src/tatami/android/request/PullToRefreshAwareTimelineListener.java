package tatami.android.request;

import android.content.Context;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.octo.android.robospice.persistence.exception.SpiceException;

public class PullToRefreshAwareTimelineListener extends TimelineListener {

	private PullToRefreshBase<?> pullToRefresh;

	public PullToRefreshAwareTimelineListener(Context context,
			PullToRefreshBase<?> pullToRefresh) {
		super(context);
		this.pullToRefresh = pullToRefresh;
	}

	@Override
	public void onRequestFailure(SpiceException se) {
		super.onRequestFailure(se);
		if (pullToRefresh.isRefreshing()) {
			pullToRefresh.onRefreshComplete();
		}
	}

	@Override
	public void onRequestSuccess(ListStatus listStatus) {
		super.onRequestSuccess(listStatus);
		if (pullToRefresh.isRefreshing()) {
			pullToRefresh.onRefreshComplete();
		}
	}
}
