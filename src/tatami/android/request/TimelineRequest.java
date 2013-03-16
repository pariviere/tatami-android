package tatami.android.request;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;

import tatami.android.Client;
import tatami.android.content.UriBuilder;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import tatami.android.sync.SyncMeta;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;


/**
 * <p>
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
		
		ContentResolver provider = context.getContentResolver();


		SimpleEntry<String, String> queryParams = null;

		if (extras != null && extras.containsKey(SyncMeta.BEFORE_ID)) {
			String beforeStatusId = extras.get(SyncMeta.BEFORE_ID);

			queryParams = new SimpleEntry<String, String>("max_id",
					beforeStatusId);
		} else {
			
			Status last = queryLastStatus(provider);
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
	
	
	/**
	 * <p>
	 * Return the last status
	 * </p>
	 * 
	 * @param provider
	 * @return
	 * @throws RemoteException
	 */
	protected Status queryLastStatus(ContentResolver provider)
			throws RemoteException {

		Uri lastUri = UriBuilder.getLastStatusUri();

		Cursor cursor = provider.query(lastUri, null, null, null, null);

		if (cursor.moveToFirst())
			return StatusFactory.fromCursorRow(cursor);
		else
			return null;
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
