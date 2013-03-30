package tatami.android.request;

import java.sql.SQLException;

import tatami.android.content.DbHelper;
import tatami.android.events.NewStatus;
import tatami.android.events.PersistTimelineDone;
import tatami.android.events.RequestFailure;
import tatami.android.model.Status;
import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.greenrobot.event.EventBus;

/**
 * <p>
 * {@link RequestListener} implementation which will insert {@link ListStatus}
 * into database.
 * </p>
 * 
 * @author pariviere
 */
public class PersistTimeline implements RequestListener<ListStatus> {
	private static final String TAG = PersistTimeline.class.getSimpleName();

	Context context;
	DbHelper helper;

	public PersistTimeline(Context context) {
		this.context = context;
		this.helper = OpenHelperManager.getHelper(context, DbHelper.class);
	}

	@Override
	public void onRequestFailure(SpiceException se) {
		EventBus.getDefault().post(new RequestFailure(this, se, null));
	}

	@Override
	public void onRequestSuccess(ListStatus listStatus) {
		try {
			Dao<Status, String> statusDao = helper.getStatusDao();

			for (Status status : listStatus) {
				String statusId = status.getStatusId();				
				if (statusDao.queryForEq("statusId", statusId).isEmpty()) {
					statusDao.create(status);
				}
				EventBus.getDefault().post(new NewStatus(status));
			}
		} catch (SQLException se) {
			Log.e(TAG, "", se);
			EventBus.getDefault().post(new RequestFailure(this, se, null));
		}

		EventBus.getDefault().post(new PersistTimelineDone());
	}
}
