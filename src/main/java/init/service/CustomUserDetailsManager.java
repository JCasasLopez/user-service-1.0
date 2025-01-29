package init.service;

import java.util.Set;
import java.util.regex.Pattern;

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
import init.exception.InvalidPasswordException;
import init.exception.RolNotFoundException;
import init.exception.UserAlreadyAdminException;
import init.utilidades.Mapeador;

@Service
public class CustomUserDetailsManager implements UserDetailsManager {
	
	//Requisitos: Al menos 8 caracteres, una letra mayúscula, una minúscula, un número y un símbolo
	private final String PASSWORD_PATTERN =
			"^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$";
	private final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
	
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
		Set<Rol> roles = usuario.getRoles();
		roles.add(rol);
		return usuario;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//Método interno usado por Spring Security durante el proceso de autenticación
		//No debe exponerse directamente a los usuarios
		if(usuariosDao.findByUsername(username)==null) {
			throw new UsernameNotFoundException("Usuario " + username + " no encontrado");
		}
		return mapeador.usuarioToUsuarioSecurity(usuariosDao.findByUsername(username));
	}

	@Override
	public void createUser(UserDetails user) {
		if (user instanceof UsuarioSecurity) {
			UsuarioSecurity usuarioSecurity = (UsuarioSecurity) user;
			Usuario usuario = usuarioSecurity.getUsuario();
			usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
			usuariosDao.save(addRole(usuario, 1));
		} else {
			throw new IllegalArgumentException("El objeto UserDetails proporcionado no es compatible");
		}
	}

	@Override
	public void updateUser(UserDetails user) {
		
	}

	@Override
	@Transactional
	@PreAuthorize("#username == authentication.principal.username")
	public void deleteUser(String username) {
		//Accesible solo al usuario mismo una vez esté autenticado
	    if(!usuariosDao.existsByUsername(username)) {
			throw new UsernameNotFoundException(("Usuario " + username + " no encontrado"));
		}
		usuariosDao.deleteByUsername(username);
	}

	@Override
	@Transactional
	@PreAuthorize("isAuthenticated()")
	public void changePassword(String oldPassword, String newPassword) {
		//Si el resultado es falso, lanza una InvalidPasswordException en el método de origen
		passwordIsValid(newPassword);
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
	
	public Usuario findUser(String username) {
		if(usuariosDao.findByUsername(username)!=null) {
			return usuariosDao.findByUsername(username);
		}
		throw new UsernameNotFoundException(("Usuario " + username + " no encontrado"));
	}
	
	@PreAuthorize("hasRole('ROLE_SUPERADMIN')")
	public void upgradeUser(Usuario usuario) {
		//Este método, solo accesible al "SUPER ADMIN", convierte usuarios normales en "admins"
		if(usuario.getRoles().size() > 1) {
			throw new UserAlreadyAdminException("El usuario ya tiene el rol ADMIN");
		}
		usuariosDao.save(addRole(usuario, 2));
	}
	
	public boolean isUserAdmin(String username) {
		//Al usuario se le asigna por defecto el role de usuario (ROLE_USER), por tanto, cuando
		//la colección de roles tiene más de un rol, significa que el usuario es también ADMIN.
		if(usuariosDao.findByUsername(username).getRoles().size() > 1) {
			return true;
		}
		return false;
	}
	
	public boolean passwordIsValid(String password) {
		boolean result = pattern.matcher(password).matches();
        if(!result) {
        	throw new InvalidPasswordException("La contraseña no cumple con los requisitos");
        }
        return result;
	}
	
}
