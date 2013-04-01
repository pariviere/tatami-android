package tatami.android.request;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;

import tatami.android.Client;
import tatami.android.content.DbHelper;
import tatami.android.model.Status;
import tatami.android.sync.SyncMeta;
import android.content.Context;

/**
 * <p>
 * Retrieves timeline from remote Tatami instance.
 * </p>
 * <p>
 * It also requires DB access in order to find most recent status.
 * </p>
 * 
 * @author pariviere
 */
public class TimelineRequest extends AuthenticatedRequest<ListStatus> {

	private HashMap<String, String> extras;

	public TimelineRequest(Context context, HashMap<String, String> extras) {
		super(context, ListStatus.class);
		this.extras = extras;
	}

	@Override
	public ListStatus doLoadDataFromNetwork() throws Exception {
		DbHelper helper = DbHelper.getDbHelpder(context);

		SimpleEntry<String, String> queryParams = null;

		if (extras != null && extras.containsKey(SyncMeta.BEFORE_ID)) {
			String beforeStatusId = extras.get(SyncMeta.BEFORE_ID);

			queryParams = new SimpleEntry<String, String>("max_id",
					beforeStatusId);
		} else {

			Status last = helper.getMostRecentStatus();
			if (last != null) {
				queryParams = new SimpleEntry<String, String>("since_id",
						last.getStatusId());
			}
		}

		List<Status> statuses = Client.getInstance().getTimeline(queryParams);

		ListStatus listStatus = new ListStatus();

		for (Status status : statuses) {
			listStatus.add(status);
		}

		return listStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String key : extras.keySet()) {
			builder.append(key + "=" + extras.get(key));
			builder.append(",");
		}

		return "TimelineRequest [extras=" + builder.toString() + "]";
	}
}
