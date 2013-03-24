package tatami.android.model;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

/**
 * 
 * @author pariviere
 */
public class StatusFactory {
	public static Status from(ContentValues contentValues) {
		Status status = new Status();

		status.setStatusId(contentValues.getAsString("statusId"));
		status.setContent(contentValues.getAsString("content"));
		status.setFirstName(contentValues.getAsString("firstName"));
		status.setLastName(contentValues.getAsString("lastName"));
		status.setUsername(contentValues.getAsString("username"));
		status.setGravatar(contentValues.getAsString("gravatar"));
		status.setHtmlContent(contentValues.getAsString("htmlContent"));
		status.setStatusDate(new Date(contentValues.getAsLong("statusDate")));
		status.setFavorite(contentValues.getAsBoolean("favorite"));
		status.setGroupId(contentValues.getAsString("groupId"));
		status.setGroupName(contentValues.getAsString("groupName"));
		status.setGroupPublic(contentValues.getAsBoolean("groupPublic"));
		status.setReplyToId(contentValues.getAsString("replyToId"));
		status.setReplyToUsername(contentValues.getAsString("replyToUsername"));
		status.setPrivate(contentValues.getAsBoolean("private"));

		return status;
	}

	public static ContentValues to(Status status) {

		ContentValues values = new ContentValues();

		values.put("statusId", status.getStatusId());
		values.put("content", status.getContent());
		values.put("firstName", status.getFirstName());
		values.put("lastName", status.getLastName());
		values.put("username", status.getUsername());
		values.put("statusDate", status.getStatusDate().getTime());
		values.put("gravatar", status.getGravatar());
		values.put("htmlContent", status.getHtmlContent());
		values.put("favorite", status.isFavorite());
		values.put("groupId", status.getGroupId());
		values.put("groupName", status.getGroupName());
		values.put("groupPublic", status.isGroupPublic());
		values.put("replyToId", status.getReplyToId());
		values.put("replyToUsername", status.getReplyToUsername());
		values.put("private", status.isPrivate());
		
		return values;
	}

	public static Status fromCursorRow(Cursor cursor) {
		
		ContentValues values = new ContentValues();
		DatabaseUtils.cursorRowToContentValues(cursor, values);
		
		return from(values);
	}
}
