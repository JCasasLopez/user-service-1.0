package init.config.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import init.dao.UsuariosDao;
import init.entities.Usuario;
import init.utilidades.StandardResponseHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    StandardResponseHandler standardResponseHandler;
    UsuariosDao usuariosDao; 

    public CustomAuthenticationEntryPoint(StandardResponseHandler standardResponseHandler,
    		UsuariosDao usuariosDao) {
        this.standardResponseHandler = standardResponseHandler;
        this.usuariosDao = usuariosDao;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
    		AuthenticationException authException) throws IOException {
    	String username = (String) request.getParameter("username");
    	Usuario usuario = usuariosDao.findByUsername(username);
    	if(usuario == null) {
    		standardResponseHandler.handleResponse(response, 404, "No existe el usuario " + username, 
    				null);
    		return; 	
    	}

    	standardResponseHandler.handleResponse(response, 401, 
    			"Acceso denegado. Token inválido o ausente", null);
    }
}
