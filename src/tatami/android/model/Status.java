package tatami.android.model;

import java.util.Date;

import com.github.rjeschke.txtmark.Processor;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "status")
public class Status {

	@DatabaseField(columnName = "_id", generatedId = true)
	private long id;
	
	@DatabaseField(unique = true, canBeNull = false, index = true)
	private String statusId;

	@DatabaseField(canBeNull = false, index = true)
	private String username;

	@DatabaseField
	private String gravatar;

	@DatabaseField
	private String firstName;

	@DatabaseField
	private String lastName;

	@DatabaseField(canBeNull = false) 
	private String content;

	
	@DatabaseField(canBeNull = false)
	private String htmlContent;

	@DatabaseField(canBeNull = false, dataType = DataType.DATE_LONG, index = true)
	private Date statusDate;
	public Status() {
	}

	public String getContent() {
		return content;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getGravatar() {
		return gravatar;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	
	public String getHtmlContent() {
		return htmlContent;
	}

	public long getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public String getStatusId() {
		return statusId;
	}

	public String getUsername() {
		return username;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setGravatar(String gravatar) {
		this.gravatar = gravatar;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "Status [statusId=" + statusId + ", username=" + username
				+ ", statusDate=" + statusDate + "]";
	}
}
