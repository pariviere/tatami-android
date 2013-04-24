package tatami.android.ui.fragment;

import tatami.android.Constants;
import tatami.android.R;
import tatami.android.content.DbHelper;
import tatami.android.model.Status;
import tatami.android.ui.widget.StatusDisplayer;
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

	RelativeLayout metaLayout;
	RelativeLayout headingLayout;
	ImageView avatar;
	TextView status;
	TextView userName;
	TextView commonName;
	TextView replyToUserName;
	TextView info;
	TextView date;
	ImageView replyToDrawable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.display_status, container, false);

		avatar = (ImageView) view.findViewById(R.id.userAvatar);
		status = (TextView) view.findViewById(R.id.status);
		commonName = (TextView) view.findViewById(R.id.userCommonName);
		userName = (TextView) view.findViewById(R.id.userUsername);
		date = (TextView) view.findViewById(R.id.statusDate);
		replyToUserName = (TextView) view.findViewById(R.id.replyToUserName);
		replyToDrawable = (ImageView) view.findViewById(R.id.replyToDrawable);
		metaLayout = (RelativeLayout) view.findViewById(R.id.statusMetaLayout);
		headingLayout = (RelativeLayout) view.findViewById(R.id.statusHeadingLayout);

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
		viewHolder.replyToDrawable = replyToDrawable;

//		StatusTextViewMapper.getInstance(getActivity()).decorate(
//				viewHolder.status, statusObj);
		displayer.buildAvatarTextView(view, viewHolder.avatar, statusObj);
		displayer.buildInfoTextView(viewHolder.info, statusObj);
		displayer.buildDateTextView(viewHolder.date, statusObj);
		displayer.buildReplyToTextView(viewHolder.replyTo, viewHolder.replyToDrawable, statusObj);

		return view;
	}
}
