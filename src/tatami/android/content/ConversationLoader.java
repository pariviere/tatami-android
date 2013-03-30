package tatami.android.content;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import tatami.android.model.Details;
import tatami.android.model.Status;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class ConversationLoader extends AsyncTaskLoader<Cursor> {
	private final static String TAG = ConversationLoader.class.getSimpleName();
	
	public Status forStatus;

	public ConversationLoader(Context context, Status forStatus) {
		super(context);
		this.forStatus = forStatus;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		forceLoad();
	}

	@Override
	public Cursor loadInBackground() {
		DbHelper helper = OpenHelperManager.getHelper(getContext(),
				DbHelper.class);

		try {
			Dao<Status, String> statusDao = helper.getStatusDao();
			Dao<Details, String> detailsDao = helper.getDetailsDao();

			
			List<Details> detailsList = detailsDao.queryForEq("detailsId",
					forStatus.getStatusId());

			List<String> ids = new ArrayList<String>();
			for (Details details : detailsList) {
				ids.add(details.getStatusId());
			}
			
			QueryBuilder<Status, String> queryBuilder = statusDao.queryBuilder();
			queryBuilder.where().in("statusId", ids);
			
			queryBuilder.orderBy("statusDate", false);
			CloseableIterator<Status> iterator = statusDao.iterator(queryBuilder
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
