package tatami.android.ui.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;

public class TagDecorator implements SpannableDecorator {

	private static TagDecorator _instance = null;
	
	public static TagDecorator getInstance() {
		if (_instance == null) {
			_instance = new TagDecorator();
		}
		
		return _instance;
	}
	
	
	private String regex = "\\B#\\w*[a-zA-Z]+\\w*";
	
	private Pattern pattern;
	
	private TagDecorator() {
		this.pattern = Pattern.compile(regex);
	}
	
	@Override
	public void decorate(SpannableStringBuilder ssb) {
		String text = ssb.toString();
		
		Matcher matcher = pattern.matcher(text);
		
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			
			ssb.setSpan(new ForegroundColorSpan(Color.GREEN), start, end, 0);
		}		
	}
}
