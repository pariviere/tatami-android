package tatami.android.ui.fragment;

import tatami.android.Constants;
import tatami.android.R;
import tatami.android.content.DbHelper;
import tatami.android.model.Status;
import tatami.android.ui.widget.StatusDisplayer;
import tatami.android.ui.widget.StatusTextViewMapper;
import android.app.Activity;
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
	TextView date;
	TextView replyTo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.display_status, container, false);

		avatar = (ImageView) view.findViewById(R.id.avatar);
		status = (TextView) view.findViewById(R.id.status);
		info = (TextView) view.findViewById(R.id.info);
		date = (TextView) view.findViewById(R.id.date);
		replyTo = (TextView) view.findViewById(R.id.replyTo);

		detailsLayout = (RelativeLayout) view.findViewById(R.id.status_layout);

		Activity activity = this.getActivity();
		long id = activity.getIntent().getLongExtra(Constants.STATUS_PARAM, 0);

		Log.d(TAG, "Selected status primary key is  " + id);

		DbHelper helper = DbHelper.getDbHelpder(getActivity());

		Status statusObj = helper.getStatus(id);

		StatusDisplayer displayer = new StatusDisplayer();

		StatusDisplayer.ViewHolder viewHolder = new StatusDisplayer.ViewHolder();
		viewHolder.avatar = avatar;
		viewHolder.status = status;
		viewHolder.info = info;
		viewHolder.date = date;
		viewHolder.replyTo = replyTo;

		StatusTextViewMapper.getInstance(getActivity()).decorate(
				viewHolder.status, statusObj);
		displayer.buildAvatarTextView(view, viewHolder.avatar, statusObj);
		displayer.buildInfoTextView(viewHolder.info, statusObj);
		displayer.buildDateTextView(viewHolder.date, statusObj);
		displayer.buildReplyToTextView(viewHolder.replyTo, statusObj);

		return view;
	}
}
