package init.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import init.config.security.UsuarioSecurity;
import init.dao.RolesDao;
import init.dao.UsuariosDao;
import init.entities.Rol;
import init.entities.Usuario;
import init.exception.RolNotFoundException;
import init.utilidades.Mapeador;

@Service
public class CustomUserDetailsManager implements UserDetailsManager {
	
	UsuariosDao usuariosDao;
	RolesDao rolesDao;
	Mapeador mapeador;
	PasswordEncoder passwordEncoder;
	
	public CustomUserDetailsManager(UsuariosDao usuariosDao, Mapeador mapeador, 
													PasswordEncoder passwordEncoder, RolesDao rolesDao) {
		this.usuariosDao = usuariosDao;
		this.mapeador = mapeador;
		this.passwordEncoder = passwordEncoder;
		this.rolesDao = rolesDao;
	}
	
	private Usuario addRole(Usuario usuario, int idRol) {
		Rol rol = rolesDao.findById(idRol)
		        .orElseThrow(() -> new RolNotFoundException("Rol no encontrado: " + idRol));
		    Set<Rol> roles = new HashSet<>();
		    roles.add(rol);
		    usuario.setRoles(roles);
		    return usuario;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//Método interno usado por Spring Security durante el proceso de autenticación
		//No debe exponerse directamente a los usuarios
		if(usuariosDao.findByUsername(username)==null) {
			throw new UsernameNotFoundException("Usuario no encontrado" + username);
		}
		return mapeador.usuarioToUsuarioSecurity(usuariosDao.findByUsername(username));
	}

	@Override
	public void createUser(UserDetails user) {
		//Accesible a cualquiera
		if(user instanceof UsuarioSecurity) {
			UsuarioSecurity usuarioSecurity = (UsuarioSecurity) user;
			Usuario usuario = usuarioSecurity.getUsuario();
			usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
			usuariosDao.save(addRole(usuario, 1));
		}else {
			throw new IllegalArgumentException("El objeto UserDetails proporcionado no es compatible");
		}
	}

	@Override
	public void updateUser(UserDetails user) {
		
	}

	@Override
	@Transactional
	@PreAuthorize("authentication.name == #username")
	public void deleteUser(String username) {
		//Accesible solo al usuario mismo una vez esté autenticado
		if(!usuariosDao.existsByUsername(username)) {
			throw new UsernameNotFoundException("Usuario no encontrado" + username);
		}
		usuariosDao.deleteByUsername(username);
	}

	@Override
	@Transactional
	@PreAuthorize("authentication.name == #username")
	public void changePassword(String oldPassword, String newPassword) {
		//Accesible solo al usuario mismo una vez esté autenticado
		
		//Obtenemos el objeto Usuario del Security Context
	    Authentication usuarioActual = SecurityContextHolder.getContext().getAuthentication();
	    if (usuarioActual == null) {
	        throw new AccessDeniedException("No hay usuario autenticado");
	    }
	    String username = usuarioActual.getName();
	    Usuario usuario = usuariosDao.findByUsername(username);
	    
	    //Comprobamos que la contraseña pasada como parámetro sea la misma que figura en la base de datos
	    if(!passwordEncoder.matches(oldPassword, usuario.getPassword())) {
	        throw new BadCredentialsException("La contraseña proporcionada no es correcta");
	    }
	    
	    //Guardamos la nueva contraseña en la base de datos, asegurándonos de que esté codificada
	    usuariosDao.updatePassword(username, passwordEncoder.encode(newPassword));
	}

	@Override
	public boolean userExists(String username) {
		//Accesible a cualquiera
		return usuariosDao.existsByUsername(username);
	}

}
