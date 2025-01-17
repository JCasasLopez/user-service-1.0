package init.exception;

public class RolNotFoundException extends RuntimeException {
	public RolNotFoundException(String message) {
        super(message);
    }
}
