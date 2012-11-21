package tatami.android.model;

import java.util.Date;

import android.content.ContentValues;



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
		status.setStatusDate(new Date());
		
		return status;
	}
	
	public static ContentValues to(Status status) {
		
		ContentValues values = new ContentValues();
		
		values.put("statusId", status.getStatusId());
		values.put("content", status.getContent());
		values.put("firstName", status.getFirstName());
		values.put("lastName", status.getLastName());
		values.put("username", status.getUsername());
		values.put("statusDate", new Date().getTime());
		values.put("gravatar", status.getGravatar());
		
		return values;
	}
}
