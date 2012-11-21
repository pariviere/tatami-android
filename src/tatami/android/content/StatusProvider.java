package tatami.android.content;

import java.sql.SQLException;

import tatami.android.Constants;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * <p>
 * </p>
 * content://tatami.android.provider/status/990099-0909-099
 * 
 * @author pariviere
 */
public class StatusProvider extends ContentProvider {
	private final static String TAG = StatusProvider.class.getSimpleName();

	private static final UriMatcher uriMatcher;

	private static final int STATUSES = 1;

	private static final int STATUS_ID = 2;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Constants.AUTHORITY, "status", STATUSES);
		uriMatcher.addURI(Constants.AUTHORITY, "status/*", STATUS_ID);
	}

	private DbHelper dbHelper;
	private Dao<Status, String> dao;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (uriMatcher.match(uri) != STATUSES) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// A map to hold the new record's values.
		ContentValues values;

		// If the incoming values map is not null, uses it for the new values.
		if (initialValues != null) {
			values = new ContentValues(initialValues);

		} else {
			// Otherwise, create a new value map
			values = new ContentValues();
		}

		Status newStatus = StatusFactory.from(values);

		try {
			dao.createIfNotExists(newStatus);

			Uri.Builder builder = new Uri.Builder();

			builder.scheme("content");
			builder.authority(Constants.AUTHORITY);
			builder.appendPath("status");
			builder.appendPath(newStatus.getStatusId());

			Uri newUri = builder.build();

			Log.d(TAG, "New URI is " + newUri.toString());

			return newUri;
		} catch (SQLException se) {
			throw new android.database.SQLException("", se);
		}

	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		dbHelper = OpenHelperManager.getHelper(context, DbHelper.class);
		try {
			dao = dbHelper.getStatusDao();
		} catch (SQLException se) {
			Log.e(TAG, "Can not initialize StatusProvider", se);
			return false;
		}

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		try {
			QueryBuilder<Status, String> queryBuilder = dao.queryBuilder();

			switch (uriMatcher.match(uri)) {
			case STATUSES:
				// nothing more to do here
				break;
			case STATUS_ID:
				String statusId = uri.getLastPathSegment();
				queryBuilder.where().eq("statusId", statusId);
				break;
			}

			CloseableIterator<Status> iterator = dao.iterator(queryBuilder
					.prepare());
			AndroidDatabaseResults results = (AndroidDatabaseResults) iterator
					.getRawResults();

			return results.getRawCursor();
		} catch (SQLException e) {
			Log.e(TAG, "Can not execute query", e);
			throw new android.database.SQLException();
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
