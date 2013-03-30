package tatami.android.content;

import java.sql.SQLException;

import tatami.android.model.Status;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;


/**
 * <p>
 * </p>
 * 
 * @author pariviere
 */
public class StatusLoader extends AsyncTaskLoader<Cursor> {
	private final static String TAG = StatusLoader.class.getSimpleName();

	public StatusLoader(Context context) {
		super(context);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		forceLoad();
	}

	@Override
	public Cursor loadInBackground() {
		
		DbHelper helper = OpenHelperManager.getHelper(
				getContext(), DbHelper.class);

		try {
			Dao<Status, String> dao = helper.getStatusDao();

			QueryBuilder<Status, String> queryBuilder = dao.queryBuilder();

			queryBuilder.orderBy("statusDate", false);
			CloseableIterator<Status> iterator = dao.iterator(queryBuilder
					.prepare());
			AndroidDatabaseResults results = (AndroidDatabaseResults) iterator
					.getRawResults();

			return results.getRawCursor();
		} catch (SQLException e) {
			Log.e(TAG, "ERROR", e);
		}
		
		return null;
	}

}
