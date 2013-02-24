package tatami.android.widget;

import tatami.android.fragment.DetailsStatus;
import android.database.ContentObserver;
import android.net.Uri;

public class DetailsObserver extends ContentObserver {
	DetailsStatus detailsStatus;

	public DetailsObserver(DetailsStatus detailsStatus) {
		super(null);
		this.detailsStatus = detailsStatus;
	}

	@Override
	public void onChange(boolean selfChange, Uri uri) {
		super.onChange(selfChange, uri);

		detailsStatus.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				detailsStatus.getLoaderManager().restartLoader(0, null,
						detailsStatus);
			}
		});
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
	}

}
