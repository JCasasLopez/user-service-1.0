package init.service;

import org.springframework.stereotype.Service;

import init.dao.UsuariosDao;
import init.entities.Usuario;

@Service
public class BlockAccountService {
	
	private static final int MAX_INTENTOS_FALLLIDOS = 3;
	
	UsuariosDao usuariosDao;
	
	public BlockAccountService(UsuariosDao usuariosDao) {
		this.usuariosDao = usuariosDao;
	}

	public void incrementarIntentosFallidos(String username) {
		Usuario usuario = usuariosDao.findByUsername(username);
		int intentosFaliidos = usuario.getIntentosFallidos();
		if(intentosFaliidos > MAX_INTENTOS_FALLLIDOS) {
			bloquearCuenta(usuario);
		}
		usuario.setIntentosFallidos(intentosFaliidos+=1);
		usuariosDao.save(usuario);
	}
	
	public void resetearIntentosFallidos(String username) {
		Usuario usuario = usuariosDao.findByUsername(username);
		usuario.setIdUsuario(0);
		usuariosDao.save(usuario);
	}
	
	public void bloquearCuenta(Usuario usuario) {
		usuario.setCuentaBloqueada(true);
		usuariosDao.save(usuario);
	}
	
	public void desbloquearCuenta(Usuario usuario) {
		usuario.setIntentosFallidos(0);
		usuario.setCuentaBloqueada(false);
		usuariosDao.save(usuario);
	}
}
