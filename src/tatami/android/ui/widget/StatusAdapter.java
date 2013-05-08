package tatami.android.ui.widget;


import java.util.Date;

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

/**
 * <p>
 * Own implementation of {@link ArrayAdapter} in order to handle {@link Status}.
 * </p>
 * 
 * 
 * 
 * @author pariviere
 */
public class StatusAdapter extends CursorAdapter {
	private static String TAG = StatusAdapter.class.getSimpleName();
	private int lastPosition = -1;
	private Date lastAnimation;

	public StatusAdapter(Context context, Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View rowView = null;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = inflater.inflate(R.layout.display_status, null);

		StatusViewHolder viewHolder = new StatusViewHolder(rowView);
		rowView.setTag(viewHolder);
		return rowView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Status status = StatusFactory.fromCursorRow(cursor);
		StatusViewHolder displayStatusViewHolder = (StatusViewHolder) view
				.getTag();
		displayStatusViewHolder.bindView(status);

//		animate(view, context, cursor);
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

			TranslateAnimation animation = new TranslateAnimation(0, 0, 200, 0);
			animation.setFillBefore(true);
			animation.setDuration(200);
			
			if (lastAnimation != null) {
				animation.setStartOffset(200);
			}
			lastAnimation = new Date();
			
			view.startAnimation(animation);
		}
	}

}
