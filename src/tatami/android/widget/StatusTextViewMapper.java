package tatami.android.widget;

import net.nightwhistler.htmlspanner.HtmlSpanner;
import tatami.android.model.Status;
import android.content.Context;
import android.support.v4.util.LruCache;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * <p>
 * Maps {@link TextView} and {@link Status} together. All formatting is made by
 * this class.
 * </p>
 * 
 * @author pariviere
 */
public class StatusTextViewMapper {
	private static String TAG = StatusTextViewMapper.class.getSimpleName();

	private LruCache<String, SpannableStringBuilder> cache;
	private static StatusTextViewMapper _instance = null;
	private HtmlSpanner htmlSpanner;

	private Context context;

	/**
	 * <p>
	 * </p>
	 * 
	 * @param context
	 * @return
	 */
	public static StatusTextViewMapper getInstance(Context context) {
		if (_instance == null) {
			_instance = new StatusTextViewMapper();

			_instance.context = context;
			_instance.cache = new LruCache<String, SpannableStringBuilder>(
					4 * 1024 * 1024);

			_instance.htmlSpanner = new HtmlSpanner();
			_instance.htmlSpanner.registerHandler("img", new AsyncImageHander(
					context));
		}

		return _instance;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param textView
	 * @param status
	 */
	public void decorate(TextView textView, Status status) {

		String statusId = status.getStatusId();

		SpannableStringBuilder ssb = cache.get(statusId);

		if (cache.get(statusId) == null) {
			Log.d(TAG, "No cache found for key = " + statusId);

			String html = status.getHtmlContent();
			ssb = (SpannableStringBuilder) htmlSpanner.fromHtml(html);

			UsernameDecorator.getInstance().decorate(ssb);
			TagDecorator.getInstance().decorate(ssb);
			URLDecorator.getInstance(context).decorate(ssb);

			cache.put(statusId, ssb);

		} else {
			Log.d(TAG, "Use cache for key = " + statusId);
		}

		textView.setText(ssb, BufferType.SPANNABLE);
		textView.setMovementMethod(null);
		textView.setOnTouchListener(new LinkDetectionListener());

		textView.setTextSize(14);
	}
}
