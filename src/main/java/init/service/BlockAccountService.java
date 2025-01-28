package init.service;

import org.springframework.stereotype.Service;

import init.dao.UsuariosDao;
import init.entities.Usuario;

@Service
public class BlockAccountService {
	
	private static final int MAX_INTENTOS_FALLIDOS = 3;
	
	UsuariosDao usuariosDao;
	
	public BlockAccountService(UsuariosDao usuariosDao) {
		this.usuariosDao = usuariosDao;
	}

	public void incrementarIntentosFallidos(Usuario usuario) {
		if(usuario.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
			bloquearCuenta(usuario);
		}
		usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
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
		usuario.setIntentosFallidos(0);
		usuario.setCuentaBloqueada(false);
		usuariosDao.save(usuario);
	}
}
