package init.exception.handler;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import init.entities.StandardResponse;
import init.exception.InvalidPasswordException;
import init.exception.NoSuchUserException;
import init.exception.UserAlreadyAdminException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<StandardResponse> handleUsernameNotFoundException(UsernameNotFoundException ex){
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), ex.getMessage(), null,
				HttpStatus.NOT_FOUND);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<StandardResponse> handleIllegalArgumentException(IllegalArgumentException ex){
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), ex.getMessage(), null,
				HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<StandardResponse> handleBadCredentialsException(BadCredentialsException ex){
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), ex.getMessage(), null,
				HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<StandardResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		if(ex.getMessage().contains("users.username")) {
			StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), 
					"Ya existe un usuario con ese username", null, HttpStatus.CONFLICT);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
		}
		if(ex.getMessage().contains("users.email")) {
			StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), 
					"Ya existe un usuario con ese email", null, HttpStatus.CONFLICT);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
		}else {
			StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), 
					"Se produjo una violación de la integridad de los datos", null, HttpStatus.CONFLICT);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
		}
	}
	
	@ExceptionHandler(UserAlreadyAdminException.class)
	public ResponseEntity<StandardResponse> handleUserAlreadyAdminException(UserAlreadyAdminException ex) {
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), ex.getMessage(), null,
				HttpStatus.CONFLICT);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
	}
	
	@ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<StandardResponse> handleExpiredJwtException(ExpiredJwtException ex) {
		StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), ex.getMessage(), null,
				HttpStatus.UNAUTHORIZED);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<StandardResponse> handleSecurityException(SecurityException ex) {
    	StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), ex.getMessage(), null,
				HttpStatus.UNAUTHORIZED);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<StandardResponse> handleMalformedJwtException(MalformedJwtException ex) {
    	StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), ex.getMessage(), null,
				HttpStatus.UNAUTHORIZED);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<StandardResponse> handleJwtException(JwtException ex) {
    	StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), ex.getMessage(), null,
				HttpStatus.UNAUTHORIZED);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
    }
    
    //Esta excepción es para el caso de que la contraseña no cumpla los requisitos de seguridad
    //(una mayúscula, una minúscula, un número y un símbolo)
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<StandardResponse> handleInvalidPasswordException (InvalidPasswordException ex) {
    	StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), ex.getMessage(), null,
				HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }
    
    @ExceptionHandler(NoSuchUserException .class)
    public ResponseEntity<StandardResponse> handleNoSuchUserException (NoSuchUserException  ex) {
    	StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), ex.getMessage(), null,
				HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardResponse> handleConstraintViolationException (ConstraintViolationException  ex) {
    	StandardResponse respuesta = new StandardResponse (LocalDateTime.now(), 
    			"Falta algún campo o formato incorrecto", null, HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }
}
