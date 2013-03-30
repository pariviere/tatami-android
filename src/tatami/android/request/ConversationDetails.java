package tatami.android.request;

public class ConversationDetails {
	private String forStatusId;
	private ListStatus conversation;

	public ConversationDetails(String forStatusId) {
		this.forStatusId = forStatusId;
		conversation = new ListStatus();
	}

	public String getForStatusId() {
		return forStatusId;
	}

	public ListStatus getConversation() {
		return conversation;
	}
}
