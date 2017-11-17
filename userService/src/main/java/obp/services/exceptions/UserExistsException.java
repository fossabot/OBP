package obp.services.exceptions;

public class UserExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserExistsException() {
		super();
	}

	public UserExistsException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
