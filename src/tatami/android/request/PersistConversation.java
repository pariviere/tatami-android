package tatami.android.request;

import java.sql.SQLException;

import tatami.android.content.DbHelper;
import tatami.android.events.PersistConversationDone;
import tatami.android.model.Details;
import tatami.android.model.Status;
import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import de.greenrobot.event.EventBus;

/**
 * <p>
 * </p>
 * 
 * @author pariviere
 */
public class PersistConversation implements
		RequestListener<ConversationDetails> {
	Context context;
	DbHelper helper;

	public PersistConversation(Context context) {
		this.context = context;
		this.helper = OpenHelperManager.getHelper(context, DbHelper.class);

	}

	@Override
	public void onRequestFailure(SpiceException se) {
	}

	@Override
	public void onRequestSuccess(ConversationDetails conversationDetails) {
		try {
			Dao<Status, String> statusDao = helper.getStatusDao();
			Dao<Details, String> detailsDao = helper.getDetailsDao();

			String forStatusId = conversationDetails.getForStatusId();
			ListStatus listStatus = conversationDetails.getConversation();

			for (Status status : listStatus) {
				//
				String statusId = status.getStatusId();
				if (statusDao.queryForEq("statusId", statusId).isEmpty()) {
					statusDao.create(status);
				}

				//
				QueryBuilder<Details, String> detailsBuilder = detailsDao
						.queryBuilder();
				detailsBuilder.where().eq("detailsId", forStatusId).and()
						.eq("statusId", statusId).query();

				if (detailsBuilder.query().isEmpty()) {
					Details details = new Details();
					details.setDetailsId(forStatusId);
					details.setStatusId(statusId);

					detailsDao.create(details);
				}
			}
			EventBus.getDefault().post(new PersistConversationDone());
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
}
