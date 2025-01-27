package init.config.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import init.service.BlockAccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	
	BlockAccountService blockAccountService;

	public CustomAuthenticationFailureHandler(BlockAccountService blockAccountService) {
		this.blockAccountService = blockAccountService;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String username = (String) request.getAttribute("username");
		blockAccountService.incrementarIntentosFallidos(username);
	}

}
