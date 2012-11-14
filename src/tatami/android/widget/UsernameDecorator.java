package tatami.android.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;


/**
 * <p>
 * 
 * </p>
 * @author pariviere
 */
public class UsernameDecorator implements SpannableDecorator {
	
	private static UsernameDecorator _instance = null;
	
	public static UsernameDecorator getInstance() {
		if (_instance == null) {
			_instance = new UsernameDecorator();
		}
		
		return _instance;
	}
	
	
	/**
	 * <p>
	 * \@username regex
	 * </p>
	 */
	private static String regex = "@([A-Za-z0-9_]+)"; 
	
	
	private Pattern pattern;
	
	private UsernameDecorator() {
		this.pattern = Pattern.compile(regex);
	}

	
	@Override
	public void decorate(SpannableStringBuilder ssb) {
		String text = ssb.toString();
		
		Matcher matcher = pattern.matcher(text);
		
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			
			ssb.setSpan(new ForegroundColorSpan(Color.RED), start, end, 0);
		}
	}

}
