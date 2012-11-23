package tatami.android.model;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

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
		status.setStatusDate(new Date(contentValues.getAsLong("statusDate")));

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

		return values;
	}

	public static Status fromCursorRow(Cursor cursor) {
		String statusId = cursor.getString(cursor.getColumnIndex("statusId"));
		String username = cursor.getString(cursor.getColumnIndex("username"));
		String content = cursor.getString(cursor.getColumnIndex("content"));
		String gravatar = cursor.getString(cursor.getColumnIndex("gravatar"));
		String lastName = cursor.getString(cursor.getColumnIndex("lastName"));
		String firstName = cursor.getString(cursor.getColumnIndex("firstName"));

		
		Status status = new Status();
		status.setStatusId(statusId);
		status.setContent(content);
		status.setUsername(username);
		status.setStatusDate(new Date());
		status.setGravatar(gravatar);
		status.setLastName(lastName);
		status.setFirstName(firstName);

		return status;
	}
}
