package tatami.android.content;

import tatami.android.Constants;

/**
 * <p>
 * Implementation of {@link android.content.UriMatcher} in order to be used in
 * {@link StatusProvider}.
 * </p>
 * <p>
 * URI pattern is the following :
 * <ul>
 * <li>content://tatami.android.provider/status/ : full query</li>
 * <li>content://tatami.android.provider/status/88838-88899 : item with statusId
 * 88838-88899</li>
 * <li>content://tatami.android.provider/status/last : most recent status</li>
 * <ul>
 * </p>
 * selection
 * 
 * @author pariviere
 */
public class UriMatcher extends android.content.UriMatcher {
	public static final int STATUSES = 1;
	public static final int STATUS_ID = 2;
	public static final int STATUS_KEY = 5;
	public static final int STATUS_LAST = 3;
	public static final int STATUS_BEFORE = 4;

	public UriMatcher() {
		super(UriMatcher.NO_MATCH);
		loadRules();
	}

	protected void loadRules() {
		addURI(Constants.AUTHORITY, "status", STATUSES);
		addURI(Constants.AUTHORITY, "status/#", STATUS_KEY);
		addURI(Constants.AUTHORITY, "status/last", STATUS_LAST);
		addURI(Constants.AUTHORITY, "status/before/*", STATUS_BEFORE);
		addURI(Constants.AUTHORITY, "status/*", STATUS_ID);
	}
}
