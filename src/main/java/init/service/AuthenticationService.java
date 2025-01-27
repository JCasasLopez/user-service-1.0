package init.service;

import java.util.regex.Pattern;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.stereotype.Service;

import init.exception.InvalidPasswordException;

@Service
public class AuthenticationService {

	//Requisitos: Al menos 8 caracteres, una letra mayúscula, una minúscula, un número y un símbolo
	private final String PASSWORD_PATTERN =
			"^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$";
	private final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

	DaoAuthenticationProvider daoAuthenticationProvider;
	JwtService jwtService;

	public AuthenticationService(DaoAuthenticationProvider daoAuthenticationProvider, JwtService jwtService) {
		this.daoAuthenticationProvider = daoAuthenticationProvider;
		this.jwtService = jwtService;
	}

	@PreAuthorize("isAuthenticated()")
	public void logout() {
		jwtService.logUserOut();
	}

	public boolean passwordValidation(String password) {
		boolean result = pattern.matcher(password).matches();
        if(!result) {
        	throw new InvalidPasswordException("La contraseña no cumple con los requisitos");
        }
        return result;
	}
}
