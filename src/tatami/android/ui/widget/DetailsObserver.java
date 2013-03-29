package tatami.android.ui.widget;

import tatami.android.ui.fragment.ConversationDetails;
import android.database.ContentObserver;
import android.net.Uri;

public class DetailsObserver extends ContentObserver {
	ConversationDetails detailsStatus;

	public DetailsObserver(ConversationDetails detailsStatus) {
		super(null);
		this.detailsStatus = detailsStatus;
	}

	@Override
	public void onChange(boolean selfChange, Uri uri) {
		super.onChange(selfChange, uri);

		detailsStatus.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (detailsStatus != null) {
					detailsStatus.getLoaderManager().restartLoader(0, null,
							detailsStatus);
				}
			}
		});
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
	}

}
