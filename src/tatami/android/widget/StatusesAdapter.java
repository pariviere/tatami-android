package tatami.android.widget;

import net.nightwhistler.htmlspanner.HtmlSpanner;
import tatami.android.R;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.text.SpannableStringBuilder;
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

/**
 * <p>
 * Own implementation of {@link ArrayAdapter} in order to handle {@link Status}.
 * </p>
 * 
 * <p>
 * Please use {@link StatusesAdapter}{@link #add(Status)} or
 * {@link StatusesAdapter}{@link #addAll(java.util.Collection)} in order to
 * populate {@link Status}.
 * </p>
 * 
 * 
 * TODO : move status view building from main thread
 * 
 * @author pariviere
 */
public class StatusesAdapter extends CursorAdapter {
	private HtmlSpanner htmlSpanner;

	public static class ViewHolder {
		public ImageView avatar;
		public TextView status;
		public TextView info;
	}

	public StatusesAdapter(Context context, Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		htmlSpanner = new HtmlSpanner();
        AjaxCallback.setNetworkLimit(2);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View rowView = null;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = inflater.inflate(R.layout.list_status, null);

		ViewHolder viewHolder = new ViewHolder();
		viewHolder.avatar = (ImageView) rowView.findViewById(R.id.avatar);
		viewHolder.status = (TextView) rowView.findViewById(R.id.status);
		viewHolder.info = (TextView) rowView.findViewById(R.id.info);

		rowView.setTag(viewHolder);
		
		return rowView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Status status = StatusFactory.fromCursorRow(cursor);

		ViewHolder viewHolder = (ViewHolder) view.getTag();

		buildStatusTextView(viewHolder.status, status);
		buildAvatarTextView(view, viewHolder.avatar, status);
		buildInfoTextView(viewHolder.info, status);
	}

	public TextView buildInfoTextView(TextView infoTextView, Status status) {

		infoTextView.setTextSize(12);

		String html = status.getFirstName() + " " + status.getLastName();

		infoTextView.setText(html);

		return infoTextView;
	}

	public ImageView buildAvatarTextView(View view, ImageView avatarImageView,
			Status status) {
		String gravatar = status.getGravatar();

		String url = "http://www.gravatar.com/avatar/" + gravatar
				+ "?s=80&d=mm";
		
		AQuery aq = new AQuery(view);
		
		Bitmap preset = aq.getCachedImage(url);

		ImageOptions options = new ImageOptions();
		options.round = 15;
		options.fileCache = true;
		options.memCache = true;
		options.preset = preset;
		options.targetWidth = 80;
		options.fallback = R.drawable.ic_mm;				
		
		aq.id(avatarImageView).image(url, options);

		return avatarImageView;
	}
	
	public TextView buildStatusTextView(TextView statusTextView, Status status) {
		statusTextView.setTextSize(14);

		String html = status.getHtmlContent();

		SpannableStringBuilder ssb = (SpannableStringBuilder) htmlSpanner
				.fromHtml(html);

		UsernameDecorator.getInstance().decorate(ssb);
		TagDecorator.getInstance().decorate(ssb);

		statusTextView.setText(ssb, BufferType.SPANNABLE);

		return statusTextView;
	}
}
