package tatami.android.ui;

import tatami.android.R;
import tatami.android.events.RequestFailure;
import tatami.android.request.AsyncRequestHandler;
import tatami.android.ui.fragment.SideMenu;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.octo.android.robospice.SpiceManager;
import com.slidingmenu.lib.SlidingMenu;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * 
 * @author pariviere
 */
public abstract class BaseFragmentActivity extends FragmentActivity {
	protected SpiceManager spiceManager = new SpiceManager(
			AsyncRequestHandler.class);

	protected void onStart() {
		spiceManager.start(this);
		super.onStart();

		EventBus.getDefault().register(this);
	}

	@Override
	protected void onStop() {
		spiceManager.shouldStop();
		super.onStop();

		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(RequestFailure requestFailure) {
		Crouton.makeText(this, requestFailure.getThrowable().getMessage(),
				Style.ALERT).show();
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		// configure the SlidingMenu
		SlidingMenu menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setShadowWidth(15);
		menu.setBehindOffset(60);
		menu.setFadeDegree(0.35f);
		menu.setMenu(R.layout.sidemenu_frame);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SideMenu()).commit();
	}
}
