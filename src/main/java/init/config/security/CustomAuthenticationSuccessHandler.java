package init.config.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import init.exception.NoSuchUserException;
import init.service.BlockAccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
	
	BlockAccountService blockAccountService;
	
	public CustomAuthenticationSuccessHandler(BlockAccountService blockAccountService) {
		this.blockAccountService = blockAccountService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String username = (String) request.getAttribute("username");
		if(username==null) {
			throw new NoSuchUserException(username);
		}
		blockAccountService.resetearIntentosFallidos(username);
	}
	
}
