package tatami.android.request;

import java.util.List;

import tatami.android.content.UriBuilder;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class ConversationDetailsListener implements RequestListener<ConversationDetails> {
	Context context;
	
	public ConversationDetailsListener(Context context) {
		this.context = context;
	}

	@Override
	public void onRequestFailure(SpiceException se) {
		
	}

	@Override
	public void onRequestSuccess(ConversationDetails conversationDetails) {
		
		ContentResolver provider = context.getContentResolver();

		
		String forStatusId = conversationDetails.getForStatusId();
		List<Status> statuses = conversationDetails.getConversation();

		Uri fullUri = UriBuilder.getFullUri();
		Uri detailsUri = UriBuilder.getDetailsUri(forStatusId);

		for (Status status : statuses) {
			ContentValues statusValues = StatusFactory.to(status);
			provider.insert(fullUri, statusValues);


			ContentValues detailsValues = new ContentValues();
			detailsValues.put("detailsId", forStatusId);
			detailsValues.put("statusId", status.getStatusId());
			provider.insert(detailsUri, detailsValues);
		}
	}
}
