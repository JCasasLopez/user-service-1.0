package init.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import init.config.security.UsuarioSecurity;
import init.dao.UsuariosDao;
import init.entities.Usuario;
import init.utilidades.Mapeador;

@Service
public class UserDetailsManagerImpl implements UserDetailsManager {
	
	@Autowired
	UsuariosDao usuariosDao;
	
	@Autowired
	Mapeador mapeador;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if(usuariosDao.findByUsername(username)==null) {
			throw new UsernameNotFoundException("Usuario no encontrado" + username);
		}
		return mapeador.usuarioToUsuarioSecurity(usuariosDao.findByUsername(username));
	}

	@Override
	public void createUser(UserDetails user) {
		if(user instanceof UsuarioSecurity) {
			UsuarioSecurity usuarioSecurity = (UsuarioSecurity) user;
			Usuario usuario = usuarioSecurity.getUsuario();
			usuariosDao.save(usuario);
		}else {
			throw new IllegalArgumentException("El objeto UserDetails proporcionado no es compatible");
		}
	}

	@Override
	public void updateUser(UserDetails user) {
		if(user instanceof UsuarioSecurity) {
			UsuarioSecurity usuarioSecurity = (UsuarioSecurity) user;
			Usuario usuario = usuarioSecurity.getUsuario();
			usuariosDao.save(usuario);
		}else {
			throw new IllegalArgumentException("El objeto UserDetails proporcionado no es compatible");
		}

	}

	@Override
	public void deleteUser(String username) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean userExists(String username) {
		// TODO Auto-generated method stub
		return false;
	}

}
