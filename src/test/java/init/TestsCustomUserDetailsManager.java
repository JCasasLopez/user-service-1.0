package init;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import init.config.security.UsuarioSecurity;
import init.dao.UsuariosDao;
import init.entities.Usuario;
import init.service.CustomUserDetailsManager;
import init.utilidades.Mapeador;

@ExtendWith(MockitoExtension.class)
public class TestsCustomUserDetailsManager {

	@Mock
	UsuariosDao usuariosDao;
	
	@Mock
	Mapeador mapeador;
	
	@Mock
	PasswordEncoder passwordEncoder;
	
	@InjectMocks
	CustomUserDetailsManager customUserDetailsManager;
	
	@Test
	@DisplayName("El usuario carga correctamente desde la base de datos")
	void loadUserByUsername_happyPath() {
		//Arrange
		Usuario usuario = new Usuario("Pepe", "12345", "Pepe García", "pg@gmail.com", LocalDate.of(1962, 3, 5));
		UsuarioSecurity usuarioSecurity = new UsuarioSecurity(usuario);
		String username = "Pepe";
		when(usuariosDao.findByUsername(username)).thenReturn(usuario);
		when(mapeador.usuarioToUsuarioSecurity(usuario)).thenReturn(usuarioSecurity);

		//Act
		UserDetails result = customUserDetailsManager.loadUserByUsername(username);
		
		//Assert
		assertAll (
					() -> assertNotNull(result), 
					() ->  assertEquals(username, result.getUsername())
				  );
        verify(usuariosDao, times(2)).findByUsername(username);
        verify(mapeador).usuarioToUsuarioSecurity(usuario);
	}
	
	@Test
	@DisplayName("El usuario NO carga correctamente")
	void loadUserByUsername_usuarioNoEncontrado() {
		//Arrange
		String username = "Pepe";
		when(usuariosDao.findByUsername(username)).thenReturn(null);

		//Act & Assert
		assertThrows(UsernameNotFoundException.class, 
				() -> {customUserDetailsManager.loadUserByUsername(username);}, 
						"Se esperaba una UsernameNotFoundException");
        verify(usuariosDao).findByUsername(username);
	}
	
	@Test
	@DisplayName("Usuario creado correctamente")
	void createUser_happyPath() {
		//Arrange
		Usuario usuario = new Usuario("Pepe", "12345", "Pepe García", "pg@gmail.com", LocalDate.of(1962, 3, 5));
		UsuarioSecurity usuarioSecurity = new UsuarioSecurity(usuario);
		
		//Act
		customUserDetailsManager.createUser(usuarioSecurity);
		
		//Assert
		verify(usuariosDao).save(usuario);
	}
	
	@Test
	@DisplayName("Usuario no se pudo crear")
	void createUser_usuarioNoCompatible() {
	//Si el UserDetails pasado como parámetro no es compatible con UsuarioSecurity, 
	//en otras palabras, si pertenece a otra implementación de UserDetails, lanza una excepción
		
		//Arrange
		User user = new User("admin", "{noop}password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

		//Act & Assert
		assertThrows(IllegalArgumentException.class, 
				() -> {customUserDetailsManager.updateUser(user);}, 
						"Se esperaba una IllegalArgumentException");
	}
	
	@Test
	@DisplayName("Usuario actualizado correctamente")
	void updateUser_happyPath() {
		//Arrange
		Usuario usuario = new Usuario("Pepe", "12345", "Pepe García", "pg@gmail.com", LocalDate.of(1962, 3, 5));
		UsuarioSecurity usuarioSecurity = new UsuarioSecurity(usuario);
		
		//Act
		customUserDetailsManager.updateUser(usuarioSecurity);
		
		//Assert
		verify(usuariosDao).save(usuario);
	}
	
	@Test
	@DisplayName("Usuario no se pudo actualizar")
	void updateUser_usuarioNoCompatible() {
	//Si el UserDetails pasado como parámetro no es compatible con UsuarioSecurity, 
	//en otras palabras, si pertenece a otra implementación de UserDetails, lanza una excepción
		
		//Arrange
		User user = new User("admin", "{noop}password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

		//Act & Assert
		assertThrows(IllegalArgumentException.class, 
				() -> {customUserDetailsManager.updateUser(user);}, 
						"Se esperaba una IllegalArgumentException");
	}
	
	@Test
	@DisplayName("Usuario borrado correctamente")
	void deleteUser_happyPath() {
		//Arrange
		String username = "Pepe";
		when(usuariosDao.existsByUsername(username)).thenReturn(true);
		
		//Act 
		customUserDetailsManager.deleteUser(username);
		
		//Assert
		verify(usuariosDao).existsByUsername(username);
		verify(usuariosDao).deleteByUsername(username);
	}
	
	@Test
	@DisplayName("Usuario no se pudo borrar")
	void deleteUser_usuarioNoEncontrado() {
		//Arrange
		String username = "Pepe";
		when(usuariosDao.existsByUsername(username)).thenReturn(false);
		
		//Act & Assert
		assertThrows(UsernameNotFoundException.class, 
				() -> {customUserDetailsManager.deleteUser(username);}, 
						"Se esperaba una UsernameNotFoundException");
		
		//Assert
		verify(usuariosDao).existsByUsername(username);
		verify(usuariosDao, never()).deleteByUsername(username);
	}
	
}
