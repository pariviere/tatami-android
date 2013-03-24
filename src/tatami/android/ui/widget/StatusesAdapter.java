package tatami.android.ui.widget;

import org.apache.commons.lang3.StringUtils;

import tatami.android.R;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.support.v4.widget.CursorAdapter;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.github.kevinsawicki.timeago.TimeAgo;

/**
 * <p>
 * Own implementation of {@link ArrayAdapter} in order to handle {@link Status}.
 * </p>
 * 
 * 
 * 
 * @author pariviere
 */
public class StatusesAdapter extends CursorAdapter {
	private static String TAG = StatusesAdapter.class.getSimpleName();

	private ImageOptions imageOptions;

	public static class ViewHolder {
		public ImageView avatar;
		public TextView status;
		public TextView info;
		public TextView date;
		public TextView replyTo;
	}

	public StatusesAdapter(Context context, Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		initAquery();
	}

	protected void initAquery() {
		AjaxCallback.setNetworkLimit(2);

		imageOptions = new ImageOptions();
		imageOptions.round = 15;
		imageOptions.fileCache = true;
		imageOptions.memCache = true;
		imageOptions.targetWidth = 80;
		imageOptions.fallback = R.drawable.ic_mm;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View rowView = null;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = inflater.inflate(R.layout.display_status, null);

		ViewHolder viewHolder = new ViewHolder();
		viewHolder.avatar = (ImageView) rowView.findViewById(R.id.avatar);
		viewHolder.status = (TextView) rowView.findViewById(R.id.status);
		viewHolder.info = (TextView) rowView.findViewById(R.id.info);
		viewHolder.date = (TextView) rowView.findViewById(R.id.date);
		viewHolder.replyTo = (TextView) rowView.findViewById(R.id.replyTo);

		rowView.setTag(viewHolder);

		return rowView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Status status = StatusFactory.fromCursorRow(cursor);

		ViewHolder viewHolder = (ViewHolder) view.getTag();

		StatusTextViewMapper.getInstance(context).decorate(viewHolder.status,
				status);
		buildAvatarTextView(view, viewHolder.avatar, status);
		buildInfoTextView(viewHolder.info, status);
		buildDateTextView(viewHolder.date, status);
		buildReplyToTextView(viewHolder.replyTo, status);
	}
	
	public TextView buildReplyToTextView(TextView replyToView, Status status) {
		

		String replyToUsername = status.getReplyToUsername();
		
		if (StringUtils.isNotEmpty(replyToUsername)) {
			replyToView.setText("in reply to @" + replyToUsername);
			replyToView.setVisibility(View.VISIBLE);
		} else {
			replyToView.setVisibility(View.GONE);
		}
		return replyToView;
	}

	public TextView buildDateTextView(TextView dateTextView, Status status) {

		TimeAgo timeAgo = new TimeAgo();
		String since = timeAgo.timeAgo(status.getStatusDate());
		
		dateTextView.setText(since);

		return dateTextView;
	}

	public TextView buildInfoTextView(TextView infoTextView, Status status) {
		infoTextView.setTextSize(12);

		String fullName = status.getFirstName() + " " + status.getLastName();
		String username = status.getUsername();

		String info = fullName + " @" + username;
		SpannableStringBuilder ssb = new SpannableStringBuilder(info);

		ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, fullName.length(), 0);
		ssb.setSpan(new StyleSpan(Typeface.ITALIC), info.lastIndexOf('@'),
				info.length(), 0);
		ssb.setSpan(new AbsoluteSizeSpan(10, true), info.lastIndexOf('@'),
				info.length(), 0);

		infoTextView.setText(ssb, BufferType.SPANNABLE);

		return infoTextView;
	}

	public ImageView buildAvatarTextView(View view, ImageView avatarImageView,
			Status status) {
		String gravatar = status.getGravatar();

		String url = "http://www.gravatar.com/avatar/" + gravatar
				+ "?s=80&d=mm";

		AQuery aq = new AQuery(view);

		Bitmap preset = aq.getCachedImage(url);
		imageOptions.preset = preset;

		aq.id(avatarImageView).image(url, imageOptions);

		return avatarImageView;
	}

}
