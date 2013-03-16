package tatami.android.request;

import java.util.ArrayList;
import java.util.List;

import tatami.android.model.Status;

public class ConversationDetails {
	private String forStatusId;
	private List<Status> conversation;

	public ConversationDetails(String forStatusId) {
		this.forStatusId = forStatusId;
		conversation = new ArrayList<Status>();
	}

	public String getForStatusId() {
		return forStatusId;
	}

	public List<Status> getConversation() {
		return conversation;
	}
}
