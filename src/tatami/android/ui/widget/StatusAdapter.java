package tatami.android.ui.widget;

import java.sql.Date;

import tatami.android.R;
import tatami.android.model.Status;
import tatami.android.model.StatusFactory;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <p>
 * Own implementation of {@link ArrayAdapter} in order to handle {@link Status}.
 * </p>
 * 
 * 
 * 
 * @author pariviere
 */
public class StatusAdapter extends CursorAdapter  {
	private static String TAG = StatusAdapter.class.getSimpleName();
	private StatusDisplayer displayer;
	private int lastPosition = -1;
	private Date lastAnimation;

	public StatusAdapter(Context context, Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		displayer = new StatusDisplayer();
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View rowView = null;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = inflater.inflate(R.layout.display_status, null);

		StatusDisplayer.ViewHolder viewHolder = new StatusDisplayer.ViewHolder();
		viewHolder.avatar = (ImageView) rowView.findViewById(R.id.avatar);
		viewHolder.status = (TextView) rowView.findViewById(R.id.status);
		viewHolder.info = (TextView) rowView.findViewById(R.id.info);
		viewHolder.date = (TextView) rowView.findViewById(R.id.date);
		viewHolder.replyTo = (TextView) rowView.findViewById(R.id.replyTo);
		viewHolder.replyToDrawable = (ImageView) rowView.findViewById(R.id.replyToDrawable);

		rowView.setTag(viewHolder);
		return rowView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Status status = StatusFactory.fromCursorRow(cursor);

		StatusDisplayer.ViewHolder viewHolder = (StatusDisplayer.ViewHolder) view
				.getTag();

		StatusTextViewMapper.getInstance(context).decorate(viewHolder.status,
				status);
		displayer.buildAvatarTextView(view, viewHolder.avatar, status);
		displayer.buildInfoTextView(viewHolder.info, status);
		displayer.buildDateTextView(viewHolder.date, status);
		displayer.buildReplyToTextView(viewHolder.replyTo, viewHolder.replyToDrawable, status);
		
		animate(view, context, cursor);
	}
	
	/**
	 * 
	 * @param view
	 * @param context
	 * @param cursor
	 */
	protected void animate(View view, Context context, Cursor cursor) {
		
		if (lastPosition <= cursor.getPosition()) {
			lastPosition = cursor.getPosition();
			
			TranslateAnimation animation = new TranslateAnimation(0, 0, 200, 0 );
			animation.setDuration(500);
			animation.setFillBefore(true);
						
			view.startAnimation(animation);
		}
	}

}
