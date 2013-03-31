package tatami.android.events;

/**
 * <p>
 * This object is intented to pass events on SQL query failure.
 * </p>
 * 
 * @author pariviere
 */
public class QueryFailure {
	private Object from;
	private Throwable throwable;
	private String msg;

	public QueryFailure(Object from, Throwable throwable, String msg) {
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
