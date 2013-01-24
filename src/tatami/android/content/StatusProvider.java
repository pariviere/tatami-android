package tatami.android.content;

import java.sql.SQLException;

import tatami.android.Constants;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
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
 * Status implements the {@link ContentProvider} API. There's available
 * </p>
 * <p>
 * content://tatami.android.provider/status/990099-0909-099
 * </p>
 * 
 * @author pariviere
 */
public class StatusProvider extends ContentProvider {
	private final static String TAG = StatusProvider.class.getSimpleName();

	private tatami.android.content.UriMatcher uriMatcher;

	private DbHelper dbHelper;
	private Dao<Status, String> dao;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (uriMatcher.match(uri) != UriMatcher.STATUSES) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		Status newStatus = StatusFactory.from(values);

		try {
			Uri.Builder builder = new Uri.Builder();

			builder.scheme("content");
			builder.authority(Constants.AUTHORITY);
			builder.appendPath("status");
			builder.appendPath(newStatus.getStatusId());

			Uri itemUri = builder.build();

			if (dao.queryForId(newStatus.getStatusId()) == null) {
				dao.create(newStatus);

				Log.d(TAG, "New record. URI is " + itemUri.toString());
			} else {
				Log.d(TAG, "Record with statusId = " + newStatus.getStatusId()
						+ " already exists");

			}
			getContext().getContentResolver().notifyChange(uri, null);
			return itemUri;

		} catch (SQLException se) {
			throw new android.database.SQLException(se.getMessage());
		}

	}

	@Override
	public boolean onCreate() {
		uriMatcher = new UriMatcher();

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
			case UriMatcher.STATUS_BEFORE:

				String lastStatusId = uri.getLastPathSegment();
				Status last = dao.queryForId(lastStatusId);

				if (last != null) {
					Log.d(TAG, "Where statusDate <= " + last.getStatusDate().getTime());
					queryBuilder.where().le("statusDate", last.getStatusDate());
				}
				
				break;
				
			case UriMatcher.STATUS_LAST:
				queryBuilder.limit(Long.valueOf("1"));
				break;
				
			case UriMatcher.STATUS_ID:
				String statusId = uri.getLastPathSegment();
				Log.d(TAG, "Fetch by status id  : " + statusId);
				queryBuilder.where().eq("statusId", statusId);
				break;
				
			case UriMatcher.STATUS_KEY:
				String primaryKey = uri.getLastPathSegment();
				Log.d(TAG, "Fetch by primary key : " + primaryKey);
				queryBuilder.where().idEq(primaryKey);
				break;
			}

			queryBuilder.orderBy("statusDate", false);
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
