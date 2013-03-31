package tatami.android.events;

import tatami.android.model.Details;

/**
 * <p>
 * POJO to trigger new details event
 * </p>
 * 
 * @author pariviere
 */
public class NewDetails {

	private Details details;

	public NewDetails(Details details) {
		this.details = details;
	}

	public Details getDetails() {
		return details;
	}
}
