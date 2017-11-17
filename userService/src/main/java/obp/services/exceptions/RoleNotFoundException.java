package obp.services.exceptions;

public class RoleNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public RoleNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RoleNotFoundException(String arg0) {
		super(arg0);
	}
	
	public RoleNotFoundException() {
		super();
	}

}
