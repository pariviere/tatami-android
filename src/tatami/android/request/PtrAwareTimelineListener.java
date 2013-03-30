package tatami.android.request;

import android.content.Context;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * <p>
 * Extends {@link PersistTimeline} in order to be able to reset
 * {@link PullToRefreshBase} state.
 * </p>
 * 
 * @author pariviere
 */
public class PtrAwareTimelineListener extends PersistTimeline {

	private PullToRefreshBase<?> pullToRefresh;

	public PtrAwareTimelineListener(Context Context,
			PullToRefreshBase<?> pullToRefresh) {
		super(Context);
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
