package tatami.android.ui.widget;

import net.nightwhistler.htmlspanner.HtmlSpanner;

import org.apache.commons.lang3.StringUtils;

import tatami.android.Client;
import tatami.android.R;
import tatami.android.content.DbHelper;
import tatami.android.model.Status;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.github.kevinsawicki.timeago.TimeAgo;

/**
 * 
 * @author pariviere
 */
public class StatusViewHolder {
	RelativeLayout headingLayout;
	ImageView userAvatar;
	TextView userCommonName;
	TextView userUsername;
	TextView status;
	TextView replyToUsername;
	RelativeLayout metaLayout;
	ImageView replyToDrawable;
	ImageView replyToAvatar;
	TextView statusDate;
	TextView replyOverview;

	AQuery aq;
	String gravatarUrl;
	ImageOptions imageOptions;
	HtmlSpanner htmlSpanner;
	DbHelper dbHelper;

	Context context;

	public StatusViewHolder(View view) {
		context = view.getContext();

		headingLayout = (RelativeLayout) view
				.findViewById(R.id.statusHeadingLayout);
		userAvatar = (ImageView) view.findViewById(R.id.userAvatar);
		userCommonName = (TextView) view.findViewById(R.id.userCommonName);
		userUsername = (TextView) view.findViewById(R.id.userUsername);
		replyToUsername = (TextView) view.findViewById(R.id.replyToUserName);
		status = (TextView) view.findViewById(R.id.status);
		metaLayout = (RelativeLayout) view.findViewById(R.id.statusMetaLayout);
		replyToDrawable = (ImageView) view.findViewById(R.id.replyToDrawable);
		replyToAvatar = (ImageView) view.findViewById(R.id.replyToAvatar);
		replyOverview = (TextView) view.findViewById(R.id.replyOverview);
		statusDate = (TextView) view.findViewById(R.id.statusDate);

		aq = new AQuery(view);

		gravatarUrl = "http://www.gravatar.com/avatar/%s?s=80&d=mm";
		imageOptions = new ImageOptions();
		htmlSpanner = new HtmlSpanner();

		htmlSpanner.registerHandler("img", new AsyncImageHander(context));
		dbHelper = DbHelper.getDbHelpder(context);
	}

	public void bindView(Status status) {
		buildReplyToTextView(status);
		buildDateTextView(status);
		buildCommonName(status);
		buildUsername(status);
		buildReplyToAvatarTextView(status);
		buildAvatarTextView(status);
		buildStatus(status);
		buildReplyOverview(status);

	}

	protected void buildStatus(Status aStatus) {
		String html = aStatus.getHtmlContent();
		SpannableStringBuilder ssb = (SpannableStringBuilder) htmlSpanner
				.fromHtml(html);

		UsernameDecorator.getInstance().decorate(ssb);
		TagDecorator.getInstance().decorate(ssb);
		URLDecorator.getInstance(context).decorate(ssb);

		status.setText(ssb, BufferType.SPANNABLE);
		status.setMovementMethod(null);
		status.setOnTouchListener(new LinkDetectionListener());
	}

	protected void buildReplyOverview(Status status) {
		
		String replyToStatusId = status.getReplyToId();
		
		Status replyToStatus = dbHelper.getStatus(replyToStatusId);
		
		if (replyToStatus != null) {
			replyOverview.setText(replyToStatus.getContent());
			replyOverview.setVisibility(View.VISIBLE);
		} else {
			replyOverview.setText("");
			replyOverview.setVisibility(View.GONE);
		}
	}

	protected void buildCommonName(Status status) {
		String fullName = status.getFirstName() + " " + status.getLastName();
		userCommonName.setText(fullName);
	}

	protected void buildUsername(Status status) {
		userUsername.setText("@" + status.getUsername());
	}

	/**
	 * 
	 * @param status
	 */
	protected void buildReplyToTextView(Status status) {
		String replyTo = status.getReplyToUsername();
		if (StringUtils.isNotEmpty(replyTo)) {
			replyToUsername.setVisibility(View.VISIBLE);
			replyToUsername.setText("@" + replyTo);
		} else {
			replyToUsername.setVisibility(View.GONE);
			replyToUsername.setText("");
		}
	}

	/**
	 * 
	 * @param status
	 */
	public void buildDateTextView(Status status) {

		TimeAgo timeAgo = new TimeAgo();
		String since = timeAgo.timeAgo(status.getStatusDate());

		statusDate.setText(since);
	}

	/**
	 * 
	 * @param status
	 */
	public void buildInfoTextView(Status status) {
		String fullName = status.getFirstName() + " " + status.getLastName();
		String username = status.getUsername();

		String info = fullName + " @" + username;
		SpannableStringBuilder ssb = new SpannableStringBuilder(info);

		ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, fullName.length(), 0);
		ssb.setSpan(new StyleSpan(Typeface.ITALIC), info.lastIndexOf('@'),
				info.length(), 0);
		ssb.setSpan(new AbsoluteSizeSpan(10, true), info.lastIndexOf('@'),
				info.length(), 0);

		userCommonName.setText(ssb, BufferType.SPANNABLE);
	}

	/**
	 * 
	 * @param view
	 * @param avatarImageView
	 * @param status
	 * @return
	 */
	public void buildReplyToAvatarTextView(Status status) {
		String replyToUsername = status.getReplyToUsername();
		
		if (! StringUtils.isEmpty(replyToUsername)) {
			String gravatar = Client.md5Hex(replyToUsername + "@ippon.fr");

			String url = String.format(gravatarUrl, gravatar);

			aq.id(replyToAvatar).image(url, imageOptions);
			
			replyToAvatar.setVisibility(View.VISIBLE);
		} else {
			replyToAvatar.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * @param view
	 * @param avatarImageView
	 * @param status
	 * @return
	 */
	public void buildAvatarTextView(Status status) {
		String gravatar = status.getGravatar();
		String url = String.format(gravatarUrl, gravatar);

		aq.id(userAvatar).image(url, imageOptions);
	}
}
