package tatami.android.request;

import tatami.android.content.DbHelper;
import tatami.android.events.PersistTimelineDone;
import tatami.android.events.RequestFailure;
import tatami.android.model.Status;
import android.content.Context;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.greenrobot.event.EventBus;

/**
 * <p>
 * {@link RequestListener} implementation which will insert {@link ListStatus}
 * into database.
 * </p>
 * 
 * @author pariviere
 */
public class PersistTimeline implements RequestListener<ListStatus> {
	private static final String TAG = PersistTimeline.class.getSimpleName();

	Context context;

	public PersistTimeline(Context context) {
		this.context = context;
	}

	@Override
	public void onRequestFailure(SpiceException se) {
		EventBus.getDefault().post(new RequestFailure(this, se, null));
	}

	@Override
	public void onRequestSuccess(ListStatus listStatus) {
		DbHelper helper = DbHelper.getDbHelpder(context);

		for (Status status : listStatus) {
			helper.createStatus(status);
		}

		EventBus.getDefault().post(new PersistTimelineDone());
	}
}
