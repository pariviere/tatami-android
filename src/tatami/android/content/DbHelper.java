package tatami.android.content;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tatami.android.events.NewDetails;
import tatami.android.events.NewStatus;
import tatami.android.events.QueryFailure;
import tatami.android.model.Details;
import tatami.android.model.Status;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.greenrobot.event.EventBus;

/**
 * <p>
 * Acts as DAO helper
 * </p>
 * 
 * @author pariviere
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {
	private final static String TAG = DbHelper.class.getSimpleName();
	public static final String DB_NAME = "tatami.db";
	public static final int DB_VERSION = 2;

	private static DbHelper helper;

	public static void initHelper(Context context) {
		helper = OpenHelperManager.getHelper(context, DbHelper.class);

		try {
			// Do ask me why but this step 
			// seems to be required.
			// If not, the onCreate method is
			// not call..
			Dao<Status, String> statusDao = helper.getStatusDao();
			Dao<Details, String> detailsDao = helper.getDetailsDao();
		} catch (SQLException se) {
			Log.e(TAG, "Unable to create DAO objects");
		}
	}

	public static void releaseHelper() {
		OpenHelperManager.releaseHelper();
		helper = null;
	}

	public static DbHelper getDbHelpder(Context context) {
		return helper;
	}
	
	
	
	private Dao<Status, String> statusDao;
	private Dao<Details, String> detailsDao;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Status.class);
			TableUtils.createTable(connectionSource, Details.class);
		} catch (SQLException se) {
			Log.e(TAG, "Unable to create database.", se);
		}

	}

	public Dao<Status, String> getStatusDao() throws SQLException {

		if (statusDao == null) {
			statusDao = getDao(Status.class);
		}

		return statusDao;

	}

	public Dao<Details, String> getDetailsDao() throws SQLException {

		if (detailsDao == null) {
			detailsDao = getDao(Details.class);
		}

		return detailsDao;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {

		if (oldVersion == newVersion) {
			Log.d(TAG, "Database to version " + newVersion
					+ ". No upgrade needed.");
		} else {
			Log.d(TAG, "Database need to be upgraded to " + newVersion);
		}
	}

	/**
	 * 
	 * @param details
	 * @return
	 */
	public boolean createDetails(Details details) {

		try {
			QueryBuilder<Details, String> detailsBuilder = detailsDao
					.queryBuilder();
			detailsBuilder.where().eq("detailsId", details.getDetailsId())
					.and().eq("statusId", details.getStatusId()).query();

			if (detailsBuilder.query().isEmpty()) {
				detailsDao.create(details);
				EventBus.getDefault().post(new NewDetails(details));
			}

			return true;
		} catch (SQLException se) {
			StringBuilder builder = new StringBuilder();
			builder.append("Can not create details ");
			builder.append(details.toString());
			builder.append(" to database :");
			builder.append(se.getMessage());

			Log.e(TAG, builder.toString(), se);

			EventBus.getDefault().post(
					new QueryFailure(this, se, builder.toString()));

			return false;
		}
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param status
	 * @return
	 */
	public boolean createStatus(Status status) {
		try {
			String statusId = status.getStatusId();
			if (statusDao.queryForEq("statusId", statusId).isEmpty()) {
				statusDao.create(status);
				EventBus.getDefault().post(new NewStatus(status));
			}

			return true;
		} catch (SQLException se) {
			StringBuilder builder = new StringBuilder();
			builder.append("Can not create status ");
			builder.append(status.toString());
			builder.append(" to database :");
			builder.append(se.getMessage());

			Log.e(TAG, builder.toString(), se);

			EventBus.getDefault().post(
					new QueryFailure(this, se, builder.toString()));

			return false;
		}
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param id
	 * @return
	 */
	public Status getStatus(long id) {

		try {
			List<Status> result = statusDao.queryForEq("_id", id);

			if (!result.isEmpty()) {
				Status status = result.get(0);
				return status;
			}
		} catch (SQLException se) {
			StringBuilder builder = new StringBuilder();
			builder.append("Unable to retrieve status ");
			builder.append("with id=" + id);
			builder.append(" from database :");
			builder.append(se.getMessage());

			Log.e(TAG, builder.toString(), se);

			EventBus.getDefault().post(
					new QueryFailure(this, se, builder.toString()));
		}

		return null;
	}
	
	/**
	 * 
	 * @param statusId
	 * @return
	 */
	public Status getStatus(String statusId) {
		try {
			List<Status> result = statusDao.queryForEq("statusId", statusId);

			if (!result.isEmpty()) {
				Status status = result.get(0);
				return status;
			}
		} catch (SQLException se) {
			StringBuilder builder = new StringBuilder();
			builder.append("Unable to retrieve status ");
			builder.append("with statusId=" + statusId);
			builder.append(" from database :");
			builder.append(se.getMessage());

			Log.e(TAG, builder.toString(), se);

			EventBus.getDefault().post(
					new QueryFailure(this, se, builder.toString()));
		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	public Status getMostRecentStatus() {
		try {
			QueryBuilder<Status, String> queryBuilder = statusDao
					.queryBuilder();
			queryBuilder.orderBy("statusDate", false);
			queryBuilder.limit(1L);

			return queryBuilder.queryForFirst();
		} catch (SQLException se) {
			StringBuilder builder = new StringBuilder();
			builder.append("Unable to retrieve most recent status");
			builder.append(" from database :");
			builder.append(se.getMessage());

			Log.e(TAG, builder.toString(), se);

			EventBus.getDefault().post(
					new QueryFailure(this, se, builder.toString()));
			return null;
		}
	}

	/**
	 * <p>
	 * Returns the list of {@link Status}es, most recent first.
	 * </p>
	 * 
	 * @return
	 */
	public Cursor findTimeline() {
		try {
			QueryBuilder<Status, String> queryBuilder = statusDao
					.queryBuilder();

			queryBuilder.orderBy("statusDate", false);
			CloseableIterator<Status> iterator = statusDao
					.iterator(queryBuilder.prepare());
			AndroidDatabaseResults results = (AndroidDatabaseResults) iterator
					.getRawResults();

			return results.getRawCursor();
		} catch (SQLException se) {
			StringBuilder builder = new StringBuilder();
			builder.append("Unable to retrieve list of statuses from database: ");
			builder.append(se.getMessage());
			Log.e(TAG, builder.toString(), se);

			EventBus.getDefault().post(
					new QueryFailure(this, se, builder.toString()));
			return null;
		}
	}

	/**
	 * <p>
	 * Finds the list of {@link Status}es corresponding to a specific
	 * {@link Status} i.e a conversation.
	 * </p>
	 * 
	 * @param forStatus
	 * @return
	 * @throws SQLException
	 */
	public Cursor findConversationForStatus(Status forStatus) {
		try {
			List<Details> detailsList = detailsDao.queryForEq("detailsId",
					forStatus.getStatusId());

			List<String> ids = new ArrayList<String>();
			for (Details details : detailsList) {
				ids.add(details.getStatusId());
			}

			QueryBuilder<Status, String> queryBuilder = statusDao
					.queryBuilder();
			queryBuilder.where().in("statusId", ids);

			queryBuilder.orderBy("statusDate", false);
			CloseableIterator<Status> iterator = statusDao
					.iterator(queryBuilder.prepare());
			AndroidDatabaseResults results = (AndroidDatabaseResults) iterator
					.getRawResults();

			return results.getRawCursor();
		} catch (SQLException se) {
			StringBuilder builder = new StringBuilder();
			builder.append("Unable to retrieve conversation from database for ");
			builder.append(forStatus.toString());

			Log.e(TAG, builder.toString(), se);

			EventBus.getDefault().post(
					new QueryFailure(this, se, builder.toString()));
			return null;
		}
	}
}
