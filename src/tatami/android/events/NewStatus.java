package tatami.android.events;

import tatami.android.model.Status;

/**
 * <p>
 * POJO to trigger new status event
 * </p>
 * 
 * @author pariviere
 */
public class NewStatus {
	Status status;

	public NewStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}
}
