package init;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import init.dao.UsuariosDao;
import init.entities.Usuario;
import jakarta.persistence.EntityManager;

@DataJpaTest
class TestsDao {
	
	@Autowired
	UsuariosDao usuariosDao;
	
	@Autowired
    private EntityManager entityManager;
	
	private Usuario saveUser1() {
		Usuario user1 = new Usuario ("Yorch123", "Password123!", "Jorge García", "jc@gmail.com", 
				LocalDate.of(1980, 11, 11));
		return usuariosDao.save(user1);
	}

	@Test
	@DisplayName("No acepta 2 usuarios con el mismo username")
	void usuario_noDeberiaAceptarUsernameRepetido() {
		//Arrange
		saveUser1();
		Usuario user2 = new Usuario ("Yorch123", "Password123!", "Jorge García", "jc3@hotmail.com", 
							LocalDate.of(1980, 11, 11));
		
		//Act & Assert
		assertThrows(DataIntegrityViolationException.class, () -> {
			usuariosDao.save(user2);
			}, "Se esperaba una DataIntegrityViolationException al intentar persistir un "
					+ "usuario con el username duplicado.");
	}
	
	@Test
	@DisplayName("No acepta 2 usuarios con el mismo email")
	void usuario_noDeberiaAceptarEmailRepetido() {
		//Arrange
		saveUser1();
		Usuario user2 = new Usuario ("Yorch12345", "Password123!", "Jorge García", "jc@gmail.com", 
							LocalDate.of(1980, 11, 11));
		
		//Act & Assert
		assertThrows(DataIntegrityViolationException.class, () -> {
			usuariosDao.save(user2);
			}, "Se esperaba una DataIntegrityViolationException al intentar persistir un "
					+ "usuario con el email duplicado.");
	}
	
	@Test
	@DisplayName("Encuentra al usuario con ese username")
	void findByUsername() {
		//Arrange
		Usuario user1 = saveUser1();
		
		//Act 
		Usuario usuarioPersistido = usuariosDao.findByUsername(user1.getUsername());
		
		//Assert
		assertAll(
				() -> assertEquals(user1.getUsername(), usuarioPersistido.getUsername(), 
						"Los usernames no coinciden"),
				() -> assertEquals(user1.getEmail(), usuarioPersistido.getEmail(), 
						"Los emails no coinciden"),
				() -> assertEquals(user1.getPassword(), usuarioPersistido.getPassword(), 
						"Las contraseñas no coinciden"),
				() -> assertEquals(user1.getNombreCompleto(), usuarioPersistido.getNombreCompleto(), 
						"Los nombres no coincide"),
				() -> assertEquals(user1.getFechaNacimiento(), usuarioPersistido.getFechaNacimiento(), 
						"Las fechas de nacimiento no coinciden"));
	}
	
	@Test
	@DisplayName("El usuario con ese username existe")
	void existsByUsername() {
		//Arrange
		saveUser1();
		
		//Act & Assert
		assertTrue(usuariosDao.existsByUsername("Yorch123"), "Se esperaba que existiera ese usuario");
	}
	
	@Test
	@DisplayName("Borra al usuario con ese username")
	void deleteByUsername() {
		//Arrange
		saveUser1();
		
		//Act 
		usuariosDao.deleteByUsername("Yorch123");
		
		//Assert
		assertFalse(usuariosDao.existsByUsername("Yorch123"), 
				"Se esperaba que se hubiera borrado ese usuario");
	}
	
	@Test
	@DisplayName("Cambia la contraseña de ese username")
	void updatePassword() {
		//Arrange
		saveUser1();
		
		//Act 
		usuariosDao.updatePassword("Yorch123", "qwerty");
		//Fuerza a llevar a cabo ahora mismo todos los cambios que tienes pendientes en la 
		//base de datos. Sin esto, JPA podría retrasar las escrituras por motivos de rendimiento
		entityManager.flush();
		//Limpia el contexto de persistencia. fuerza que el siguiente findByUsername() traiga 
		//los datos actualizados de la base de datos, en lugar de usar una versión anterior que 
		//pudiera estar en caché (contexto de persistencia)
		entityManager.clear();
		
		//Assert
		assertEquals("qwerty", usuariosDao.findByUsername("Yorch123").getPassword(), 
				"Se esperaba que la contraseña fuera 'qwerty'");
	}
	
}
