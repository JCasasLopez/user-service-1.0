package init.exception;

import org.springframework.security.core.AuthenticationException;

public class NoSuchUserException extends AuthenticationException{
	public NoSuchUserException(String message) {
        super(message);
    }
}
