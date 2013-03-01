package tatami.android;

import tatami.android.model.Status;
import tatami.android.task.SendUpdate;
import tatami.android.task.UpdateListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>
 * Prompt user for writing is new status
 * </p>
 * 
 * @author pariviere
 */
public class ComposeDialog extends DialogFragment {
	private EditText composeEdit;
	private TextView countText;
	private TatamiApp app;

	public Dialog onCreateDialog(Bundle savedInstanceState) {

		app = (TatamiApp) getActivity().getApplication();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.dialog_compose, null);
		composeEdit = (EditText) view.findViewById(R.id.composeEditText);
		countText = (TextView) view.findViewById(R.id.countText);
		countText.setText("750");

		composeEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int left = 750 - s.length();
				String count = String.valueOf(left);
				countText.setText(count);
			}
		});

		builder.setView(view)
				.setPositiveButton("Post!",
						new SuccessDialogLister(this.getActivity()))
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ComposeDialog.this.getDialog().cancel();

							}
						});

		return builder.create();
	}

	private class SuccessDialogLister implements
			DialogInterface.OnClickListener, UpdateListener {
		private Activity activity;

		public SuccessDialogLister(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onUpdateFailed() {
			Toast.makeText(activity, "Update failed", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onUpdateSuceed() {
			Toast.makeText(activity, "Posted!", Toast.LENGTH_LONG).show();
			ComposeDialog.this.dismiss();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {

			if (!app.isConnected()) {
				return;
			}

			if (composeEdit.getText().length() != 0) {
				Status status = new Status();
				status.setContent(composeEdit.getText().toString());

				SendUpdate sendUpdate = new SendUpdate(activity, this);
				sendUpdate.execute(status);
			}
		}
	}
}
