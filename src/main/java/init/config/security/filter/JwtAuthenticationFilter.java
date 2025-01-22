package init.config.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import init.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	JwtService jwtService;
	
	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        String token = authHeader.substring(7);
	        //Si esta línea no lanza una excepción, significa que el token es válido
			//por lo tanto, podemos establecer el objeto authentication en el SecurityContextHolder
	        String username = jwtService.extractPayload(token).getSubject();
	        Authentication authentication = new UsernamePasswordAuthenticationToken(username, token);
	        SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);     
		} else {
			filterChain.doFilter(request, response);
			return;
		}
	}

}
