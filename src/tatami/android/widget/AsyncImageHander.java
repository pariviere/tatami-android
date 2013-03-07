package tatami.android.widget;

import net.nightwhistler.htmlspanner.handlers.ImageHandler;
import tatami.android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AsyncImageHander extends ImageHandler {

	private Context context;

	public AsyncImageHander(Context context) {
		this.context = context;
	}

	@Override
	protected Bitmap loadBitmap(String url) {
		// TODO : for now it returns a static bitmap.
		return BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_mm);
	}
}
