package tatami.android.ui.widget;

import tatami.android.model.Status;

import com.github.kevinsawicki.timeago.TimeAgo;

/**
 * 
 * @author pariviere
 */
public class StatusDateMapper {

	public void decorate(StatusViewHolder viewHolder, Status status) {
		TimeAgo timeAgo = new TimeAgo();
		String since = timeAgo.timeAgo(status.getStatusDate());

		viewHolder.statusDate.setText(since);
	}
}
