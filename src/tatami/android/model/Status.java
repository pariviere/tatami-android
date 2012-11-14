package tatami.android.model;



import java.util.Date;

import com.github.rjeschke.txtmark.Processor;


public class Status {
	private String statusId;
	private String username;
	private String gravatar;
	private String firstName;
	private String content;
	private String htmlContent;
	private Date  statusDate;
	
	public Date getStatusDate() {
		return statusDate;
	}
	
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getHtmlContent() {
		if (htmlContent == null) {
			String cleanContent = content.replaceAll("#", "\\\\#");
			htmlContent = Processor.process(cleanContent);
		}

		return htmlContent;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGravatar() {
		return gravatar;
	}

	public void setGravatar(String gravatar) {
		this.gravatar = gravatar;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	private String lastName;

	@Override
	public String toString() {
		return "Status [statusId=" + statusId + ", username=" + username + "]";
	}
}
