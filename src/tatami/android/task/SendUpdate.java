package tatami.android.task;

import tatami.android.Client;
import tatami.android.Constants;
import tatami.android.model.Status;
import tatami.android.sync.SyncAdapter;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;

/**
 * <p>
 * Handle status update.
 * </p>
 * 
 * TODO move this code into {@link SyncAdapter}
 * 
 * @author pariviere
 */
public class SendUpdate extends AsyncTask<Status, Integer, Boolean> {
	private UpdateListener updateListener;
	private Context context;
	private String statusId;

	public SendUpdate(Context context, UpdateListener updateListener,
			String statusId) {
		this.context = context;
		this.updateListener = updateListener;
		this.statusId = statusId;

	}

	@Override
	protected Boolean doInBackground(tatami.android.model.Status... params) {

		AccountManager accountManager = AccountManager.get(context);

		Account[] accounts = accountManager
				.getAccountsByType(Constants.ACCOUNT_TYPE);

		if (accounts != null) {
			Account currentAccount = accounts[0];
			String login = currentAccount.name;
			String passwd = accountManager.getPassword(currentAccount);

			for (tatami.android.model.Status status : params) {
				try {
					Client.getInstance().authenticate(login, passwd);

					if (statusId != null) {
						Client.getInstance().discussion(status, statusId);
					} else {
						Client.getInstance().update(status);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
			return true;
		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean succeed) {
		if (succeed) {
			updateListener.onUpdateSuceed();
		} else {
			updateListener.onUpdateFailed();
		}
	}
}
