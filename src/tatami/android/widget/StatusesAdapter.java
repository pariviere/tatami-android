package tatami.android.widget;

import net.nightwhistler.htmlspanner.HtmlSpanner;
import tatami.android.R;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.text.SpannableStringBuilder;
import android.util.Log;
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
 * 
 * 
 * @author pariviere
 */
public class StatusesAdapter extends CursorAdapter {
	private static String TAG = StatusesAdapter.class.getSimpleName();

	private int selected = -1;
	private HtmlSpanner htmlSpanner;
	private ImageOptions imageOptions;
	private Context context;
	private LruCache<String, SpannableStringBuilder> spanCache;

	public static class ViewHolder {
		public ImageView avatar;
		public TextView status;
		public TextView info;
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

	public StatusesAdapter(Context context, Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		this.context = context;
		spanCache = new LruCache<String, SpannableStringBuilder>(50);

		htmlSpanner = new HtmlSpanner();
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

		StatusTextViewMapper.getInstance(context).decorate(viewHolder.status, status);
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
		imageOptions.preset = preset;

		aq.id(avatarImageView).image(url, imageOptions);

		return avatarImageView;
	}

}
