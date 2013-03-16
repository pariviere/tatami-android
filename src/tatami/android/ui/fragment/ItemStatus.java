package tatami.android.ui.fragment;

import tatami.android.Constants;
import tatami.android.R;
import tatami.android.content.UriBuilder;
import tatami.android.ui.widget.StatusesAdapter;
import tatami.android.ui.widget.StatusesAdapter.ViewHolder;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ItemStatus extends Fragment {
	private final static String TAG = ItemStatus.class.getSimpleName();

	RelativeLayout detailsLayout;
	ImageView avatar;
	TextView status;
	TextView info;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_status, container, false);

		avatar = (ImageView) view.findViewById(R.id.avatar);
		status = (TextView) view.findViewById(R.id.status);
		info = (TextView) view.findViewById(R.id.info);

		detailsLayout = (RelativeLayout) view.findViewById(R.id.status_layout);

		Activity activity = this.getActivity();
		long id = activity.getIntent().getLongExtra(Constants.STATUS_PARAM, 0);

		Log.d(TAG, "Selected status primary key is  " + id);

		Cursor cursor = activity.getContentResolver().query(
				UriBuilder.getStatusUri(id), null, null, null, null);

		try {
			// Only to mimic CursorAdapter behavior and
			// to reuse StatusesAdapter code in this case
			// where there's no ListView
			StatusesAdapter adapter = new StatusesAdapter(this.getActivity(),
					cursor);

			View rowView = detailsLayout;
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.avatar = avatar;
			viewHolder.status = status;
			viewHolder.info = info;

			rowView.setTag(viewHolder);

			adapter.bindView(rowView, this.getActivity(), cursor);
		} finally {
			cursor.close();
		}

		return view;
	}
}
