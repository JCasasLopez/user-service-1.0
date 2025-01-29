package init.service;

import org.springframework.stereotype.Service;

import init.dao.UsuariosDao;
import init.entities.Usuario;
import init.exception.NoSuchUserException;
import init.utilidades.Constants;

@Service
public class BlockAccountService {
		
	UsuariosDao usuariosDao;
	
	public BlockAccountService(UsuariosDao usuariosDao) {
		this.usuariosDao = usuariosDao;
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
		usuariosDao.save(usuario);
	}
	
	public void desbloquearCuenta(String username) {
		Usuario usuario = usuariosDao.findByUsername(username);
		if(usuario == null) {
			throw new NoSuchUserException("El usuario " + username + " no existe");
		}
		usuario.setIntentosFallidos(0);
		usuario.setCuentaBloqueada(false);
		usuariosDao.save(usuario);
	}
}
