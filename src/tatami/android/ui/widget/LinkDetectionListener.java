package tatami.android.ui.widget;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

/**
 * <p>
 * {@link ClickableSpan} or {@link URLSpan} overrides click detection when used in a {@link TextView},
 * This {@link OnTouchListener} is intented to solve this behavior
 * </p>
 * 
 * From : http://stackoverflow.com/questions/7236840/android-textview-linkify-
 * intercepts-with-parent-view-gestures
 * 
 * @author pariviere
 * 
 */
public class LinkDetectionListener implements OnTouchListener {

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		TextView widget = (TextView) v;
		Object text = widget.getText();
		if (text instanceof Spanned) {
			Spannable buffer = (Spannable) text;

			int action = event.getAction();

			if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();

				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();

				x += widget.getScrollX();
				y += widget.getScrollY();

				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);

				URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);

				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						link[0].onClick(widget);
					} else if (action == MotionEvent.ACTION_DOWN) {
						Selection.setSelection(buffer,
								buffer.getSpanStart(link[0]),
								buffer.getSpanEnd(link[0]));
					}
					return true;
				}
			}

		}

		return false;
	}
}
