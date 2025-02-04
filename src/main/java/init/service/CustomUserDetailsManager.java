package init.service;

import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.security.access.prepost.PreAuthorize;
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
import init.exception.NoSuchUserException;
import init.exception.RolNotFoundException;
import init.exception.UserAlreadyAdminException;
import init.utilidades.Mapeador;

@Service
public class CustomUserDetailsManager implements UserDetailsManager {
	
	//Requisitos: Al menos 8 caracteres, una letra mayúscula, una minúscula, un número y un símbolo
	String PASSWORD_PATTERN =
			"^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$";
	Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
	
	String urlAngular = "http://www.aulas.com/auth/reset-password";
	
	UsuariosDao usuariosDao;
	RolesDao rolesDao;
	Mapeador mapeador;
	PasswordEncoder passwordEncoder;
	JwtService jwtService;
	EmailService emailService;
	
	public CustomUserDetailsManager(UsuariosDao usuariosDao, Mapeador mapeador, 
													PasswordEncoder passwordEncoder, RolesDao rolesDao,
													JwtService jwtService, EmailService emailService) {
		this.usuariosDao = usuariosDao;
		this.mapeador = mapeador;
		this.passwordEncoder = passwordEncoder;
		this.rolesDao = rolesDao;
		this.jwtService = jwtService;
		this.emailService = emailService;
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
		return mapeador.usuarioToUsuarioSecurity(findUser(username));
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
		findUser(username);
		usuariosDao.deleteByUsername(username);
	}

	@Override
	@Transactional
	public void changePassword(String oldPassword, String newPassword) {
		//Si el resultado es falso, lanza una InvalidPasswordException en el método de origen
		passwordIsValid(newPassword);
		//Obtenemos el objeto Usuario del Security Context, sabemos que este usuario existe 
		//y tiene  que estar en el SecurityContext porque este método sólo es accesible 
		//para usuarios autenticados (ver securityFilterChain)
	    Authentication usuarioActual = SecurityContextHolder.getContext().getAuthentication();
	    String username = usuarioActual.getName();
	    
	    //Comprobamos que la contraseña pasada como parámetro sea la misma que figura en la base de datos
	    if(!passwordEncoder.matches(oldPassword, findUser(username).getPassword())) {
	        throw new InvalidPasswordException("La contraseña actual no coincide con la que figura en la base de datos");
	    }
	    
	    //Guardamos la nueva contraseña en la base de datos, asegurándonos de que esté codificada
	    usuariosDao.updatePassword(username, passwordEncoder.encode(newPassword));
	}

	@Override
	public boolean userExists(String username) {
		return usuariosDao.existsByUsername(username);
	}
	
	public Usuario findUser(String username) {
		//Todas las llamadas solicitando el objeto Usuario a partir del username, se centralizan 
		//en este método
		return usuariosDao.findByUsername(username)
	            .orElseThrow(() -> new NoSuchUserException("No existe ningún usuario con ese username"));
	}
	
	@PreAuthorize("hasRole('ROLE_SUPERADMIN')")
	public void upgradeUser(Usuario usuario) {
		//Este método, solo es accesible al "SUPER ADMIN", convierte usuarios normales en "admins"
		if(usuario.getRoles().size() > 1) {
			throw new UserAlreadyAdminException("El usuario ya tiene el rol ADMIN");
		}
		usuariosDao.save(addRole(usuario, 2));
	}
	
	public boolean passwordIsValid(String password) {
		boolean result = pattern.matcher(password).matches();
        if(!result) {
        	throw new InvalidPasswordException("La contraseña no cumple con los requisitos");
        }
        return result;
	}
	
	public void forgotPassword(String email) {
		//Manda un email con la dirección del endpoint para resetear la contraseña, junto con el 
		//token JWT que usamos como autenticación
		Usuario usuario = usuariosDao.findByEmail(email)
				.orElseThrow(() -> new NoSuchUserException("No existe ningún usuario con ese username"));
		String token = jwtService.createTokenResetPassword(usuario);
		String mensaje = "Aquí tiene el enlace para la recuparación de su contraseña: " 
				+ urlAngular + "?token=" + token;
		emailService.enviarCorreo(email, "Recuperación de contraseña", mensaje);
	}
	
	public void resetPassword(String token, String newPassword) {
		//Si esta línea no lanza una excepción, significa que el token es válido
		//Obtenemos el username, con el que después obtendremos el objeto usuario, y así 
		//poder cambiar su contraseña (que codificamos con passwordEncoder)
		String username = jwtService.extractPayload(token).getSubject();
		Usuario usuario = findUser(username);
		usuario.setPassword(passwordEncoder.encode(newPassword));
		usuariosDao.save(usuario);

		//Se invalida por motivos de seguridad
		jwtService.invalidateResetToken(token);
	}
	
}
