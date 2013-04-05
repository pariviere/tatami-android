package tatami.android.ui.widget;

import org.apache.commons.lang3.StringUtils;

import tatami.android.R;
import tatami.android.model.Status;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.github.kevinsawicki.timeago.TimeAgo;


/**
 * <p>
 * </p>
 * @author pariviere
 */
public class StatusDisplayer {
	private ImageOptions imageOptions;

	public StatusDisplayer() {
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

	public static class ViewHolder {
		public ImageView avatar;
		public TextView status;
		public TextView info;
		public TextView date;
		public TextView replyTo;
		public ImageView replyToDrawable;
	}

	public TextView buildReplyToTextView(TextView replyToView, ImageView replyToDrawable, Status status) {

		String replyToUsername = status.getReplyToUsername();

		if (StringUtils.isNotEmpty(replyToUsername)) {
			replyToView.setText("to @" + replyToUsername);
			replyToView.setVisibility(View.VISIBLE);
			replyToDrawable.setVisibility(View.VISIBLE);

		} else {
			replyToView.setVisibility(View.GONE);
			replyToDrawable.setVisibility(View.GONE);
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
