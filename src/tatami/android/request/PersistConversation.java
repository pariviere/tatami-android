package tatami.android.request;

import tatami.android.content.DbHelper;
import tatami.android.events.PersistConversationDone;
import tatami.android.model.Details;
import tatami.android.model.Status;
import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.greenrobot.event.EventBus;

/**
 * <p>
 * </p>
 * 
 * @author pariviere
 */
public class PersistConversation implements
		RequestListener<ConversationDetails> {
	Context context;
	DbHelper helper;

	public PersistConversation(Context context) {
		this.context = context;
		this.helper = OpenHelperManager.getHelper(context, DbHelper.class);

	}

	@Override
	public void onRequestFailure(SpiceException se) {
	}

	@Override
	public void onRequestSuccess(ConversationDetails conversationDetails) {
		DbHelper helper = DbHelper.getDbHelpder(context);

		String forStatusId = conversationDetails.getForStatusId();
		ListStatus listStatus = conversationDetails.getConversation();

		for (Status status : listStatus) {
			helper.createStatus(status);

			String statusId = status.getStatusId();

			// current status IS part of
			// the selected status conversation
			Details details = new Details();
			details.setDetailsId(forStatusId);
			details.setStatusId(statusId);
			helper.createDetails(details);

			// the reverse is also true.
			// the select status IS part of
			// the current status conversation
			Details reverse = new Details();
			reverse.setDetailsId(statusId);
			reverse.setStatusId(forStatusId);
			helper.createDetails(reverse);

			for (Status reserveStatus : listStatus) {
				// BUT the whole status list is ALSO
				// part of the current status conversation
				if (reserveStatus != status) {
					Details reverseDetails = new Details();
					reverseDetails.setDetailsId(statusId);
					reverseDetails.setStatusId(reserveStatus.getStatusId());

					helper.createDetails(reverseDetails);
				}
			}
		}

		EventBus.getDefault().post(new PersistConversationDone());
	}
}
