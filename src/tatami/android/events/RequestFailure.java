package tatami.android.events;

/**
 * <p>
 * Just a sample bean to carry informations about request failure.
 * </p>
 * 
 * @author pariviere
 */
public class RequestFailure {
	private Object from;
	private Throwable throwable;
	private String msg;

	public RequestFailure(Object from, Throwable throwable, String msg) {
		this.from = from;
		this.throwable = throwable;
		this.msg = msg;
	}

	public Object getFrom() {
		return from;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public String getMsg() {
		return msg;
	}
}
