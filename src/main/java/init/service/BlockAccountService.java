package init.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import init.dao.UsuariosDao;
import init.entities.Usuario;
import init.events.UsuarioBloqueadoEvent;
import init.utilidades.Constants;

@Service
public class BlockAccountService {
		
	UsuariosDao usuariosDao;
	CustomUserDetailsManager customUserDetailsManager;
	ApplicationEventPublisher eventPublisher;
	
	public BlockAccountService(UsuariosDao usuariosDao, CustomUserDetailsManager customUserDetailsManager,
			ApplicationEventPublisher eventPublisher) {
		this.usuariosDao = usuariosDao;
		this.customUserDetailsManager = customUserDetailsManager;
		this.eventPublisher = eventPublisher;
	}

	public void incrementarIntentosFallidos(Usuario usuario) {
		usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
		if(usuario.getIntentosFallidos() >= Constants.MAX_INTENTOS_FALLIDOS) {
			bloquearCuenta(usuario);
			return;
		}
		usuariosDao.save(usuario);
	}

	public void resetearIntentosFallidos(Usuario usuario) {
		usuario.setIntentosFallidos(0);
		usuariosDao.save(usuario);
	}
	
	public void bloquearCuenta(Usuario usuario) {
		usuario.setCuentaBloqueada(true);
		eventPublisher.publishEvent(new UsuarioBloqueadoEvent(usuario.getUsername(), usuario.getEmail()));
		usuariosDao.save(usuario);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPERADMIN')")
	public void desbloquearCuenta(String username) {
		Usuario usuario = customUserDetailsManager.findUser(username);
	    usuario.setIntentosFallidos(0);
	    usuario.setCuentaBloqueada(false);
	    usuariosDao.save(usuario);
	}
}
