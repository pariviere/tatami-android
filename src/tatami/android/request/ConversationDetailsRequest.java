package tatami.android.request;

import java.util.List;

import tatami.android.Client;
import tatami.android.model.Status;
import android.content.Context;

/**
 * 
 * @author pariviere
 */
public class ConversationDetailsRequest extends AuthenticatedRequest<ConversationDetails> {

	private String forStatusId;

	public ConversationDetailsRequest(Context context, String forStatusId) {
		super(context, ConversationDetails.class);
		this.forStatusId = forStatusId;
	}

	@Override
	public ConversationDetails doLoadDataFromNetwork() throws Exception {

		List<Status> statuses = Client.getInstance().getDetails(forStatusId);

		ConversationDetails conversationDetails = new ConversationDetails(
				forStatusId);
		conversationDetails.getConversation().addAll(statuses);

		return conversationDetails;
	}
	
	@Override
	public String toString() {
		return "ConversationDetailsRequest[forStatusId=" + forStatusId + "]";
	}
}
