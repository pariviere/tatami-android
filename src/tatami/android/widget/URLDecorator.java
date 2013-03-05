package tatami.android.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;
import android.view.View;

public class URLDecorator implements SpannableDecorator {
	private Context context;

	public void setContext(Context context) {
		this.context = context;
	}
	
	private static URLDecorator _instance = null;

	public static URLDecorator getInstance(Context context) {
		if (_instance == null) {
			_instance = new URLDecorator();
		}
		
		_instance.setContext(context);

		return _instance;
	}

	private String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	private Pattern pattern;

	private URLDecorator() {
		this.pattern = Pattern.compile(regex);
	}

	@Override
	public void decorate(SpannableStringBuilder ssb) {
		String text = ssb.toString();

		Matcher matcher = pattern.matcher(text);

		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			;

			URLSpan urlSpan = new URLSpan(matcher.group()) {
				@Override
				public void onClick(View widget) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(getURL()));
					context.startActivity(i);
				}
			};
			
			ssb.setSpan(urlSpan, start, end, 0);
		}
	}
}
