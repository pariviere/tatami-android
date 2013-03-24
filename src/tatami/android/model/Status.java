package tatami.android.model;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "status")
public class Status {

	@DatabaseField(canBeNull = false)
	private String content;

	@DatabaseField
	private String firstName;

	@DatabaseField
	private String gravatar;

	@DatabaseField
	private String groupId;

	@DatabaseField
	private String groupName;

	@DatabaseField(canBeNull = false)
	private String htmlContent;

	@DatabaseField(columnName = "_id", generatedId = true)
	private long id;

	@DatabaseField(columnName = "favorite")
	private boolean isFavorite;

	@DatabaseField(columnName = "groupPublic")
	private boolean isGroupPublic;

	@DatabaseField(columnName = "private")
	private boolean isPrivate;

	@DatabaseField
	private String lastName;

	@DatabaseField
	private String replyToId;

	@DatabaseField
	private String replyToUsername;

	@DatabaseField(canBeNull = false, dataType = DataType.DATE_LONG, index = true)
	private Date statusDate;

	@DatabaseField(unique = true, canBeNull = false, index = true)
	private String statusId;

	@DatabaseField(canBeNull = false, index = true)
	private String username;

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

	public String getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
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

	public String getReplyToId() {
		return replyToId;
	}

	public String getReplyToUsername() {
		return replyToUsername;
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

	public boolean isFavorite() {
		return isFavorite;
	}

	public boolean isGroupPublic() {
		return isGroupPublic;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setGravatar(String gravatar) {
		this.gravatar = gravatar;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setGroupPublic(boolean isGroupPublic) {
		this.isGroupPublic = isGroupPublic;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public void setReplyToId(String replyToId) {
		this.replyToId = replyToId;
	}

	public void setReplyToUsername(String replyToUsername) {
		this.replyToUsername = replyToUsername;
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
