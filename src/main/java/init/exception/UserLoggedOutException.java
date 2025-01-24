package init.exception;

public class UserLoggedOutException extends RuntimeException {
	public UserLoggedOutException(String message) {
        super(message);
    }
}
