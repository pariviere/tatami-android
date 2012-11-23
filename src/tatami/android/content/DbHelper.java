package tatami.android.content;

import java.sql.SQLException;

import tatami.android.model.Status;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DbHelper extends OrmLiteSqliteOpenHelper {
	private final static String TAG = DbHelper.class.getSimpleName();

	private Dao<Status, String> statusDao;

	public DbHelper(Context context) {
		super(context, DbMeta.DB_NAME, null, DbMeta.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Status.class);
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

}
