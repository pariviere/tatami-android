package tatami.android.ui.fragment;

import tatami.android.R;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <p>
 * </p>
 * 
 * @author pariviere
 */
public class SideMenu extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sidemenu_list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		SlidingMenuAdapter menuAdapter = new SlidingMenuAdapter(getActivity());
		menuAdapter.add(new SlidingMenuItem("Timeline", android.R.drawable.ic_menu_myplaces));

		setListAdapter(menuAdapter);

		getListView().setBackgroundColor(Color.BLACK);
		getListView().getBackground().setAlpha(230);
	}

	private class SlidingMenuItem {
		public String tag;
		public int iconRes;

		public SlidingMenuItem(String tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}

	public class SlidingMenuAdapter extends ArrayAdapter<SlidingMenuItem> {

		public SlidingMenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.sidemenu_row, null);
			}
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView
					.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);
			return convertView;
		}
	}
}
