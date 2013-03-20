package tatami.android.request;

import tatami.android.content.UriBuilder;
import tatami.android.events.RequestFailure;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author pariviere
 */
public class TimelineListener implements RequestListener<ListStatus> {
	Context context;

	public TimelineListener(Context context) {
		this.context = context;
	}

	@Override
	public void onRequestFailure(SpiceException se) {
		EventBus.getDefault().post(new RequestFailure(this, se, null));
	}

	@Override
	public void onRequestSuccess(ListStatus listStatus) {

		ContentResolver provider = context.getContentResolver();
		Uri fullUri = UriBuilder.getFullUri();

		for (Status status : listStatus) {
			ContentValues statusValues = StatusFactory.to(status);
			provider.insert(fullUri, statusValues);
		}
	}
}
