package init.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
	
	DaoAuthenticationProvider daoAuthenticationProvider;
	JwtService jwtService;
	
	public AuthenticationService(DaoAuthenticationProvider daoAuthenticationProvider, JwtService jwtService) {
		this.daoAuthenticationProvider = daoAuthenticationProvider;
		this.jwtService = jwtService;
	}

	public String login(String username, String password) {
		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
		
		try {
			//Si esta línea no lanza una excepción, es que las credenciales son correctas,
			//por lo tanto, podemos establecer el objeto authenticated en el SecurityContextHolder
			Authentication authenticated = daoAuthenticationProvider.authenticate(authentication);
	        SecurityContextHolder.getContext().setAuthentication(authenticated);
	        return jwtService.createToken();
	        
		} catch (AuthenticationException ex) {
	        throw new BadCredentialsException("Las credenciales proporcionadas no son correctas");
		}
		
	}
	
	//@PreAuthorize("isAuthenticated()")
	public void logout() {
		jwtService.logUserOut();
	}
}
