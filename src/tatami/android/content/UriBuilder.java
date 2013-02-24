package tatami.android.content;

import tatami.android.Constants;
import android.net.Uri;

/**
 * <p>
 * </p>
 * 
 * @author pariviere
 */
public class UriBuilder {
	public static Uri getFullUri() {
		String uri = String.format("content://%s/status", Constants.AUTHORITY);
		return Uri.parse(uri);
	}
	
	public static Uri getStatusUri(String statusId) {
		String uri = String.format("content://%s/status/%s", Constants.AUTHORITY, statusId);
		return Uri.parse(uri);
	}
	
	public static Uri getStatusUri(long statusKey) {
		String uri = String.format("content://%s/status/%d", Constants.AUTHORITY, statusKey);
		return Uri.parse(uri);
	}
	
	public static Uri getLastStatusUri() {
		String uri = String.format("content://%s/status/last", Constants.AUTHORITY);
		return Uri.parse(uri);
	}
	
	public static Uri getDetailsUri(String statusId) {
		String uri = String.format("content://%s/status/details/%s", Constants.AUTHORITY, statusId);
		return Uri.parse(uri);
	}
}
