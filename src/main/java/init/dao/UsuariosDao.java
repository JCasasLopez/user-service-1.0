package init.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import init.entities.Usuario;

public interface UsuariosDao extends JpaRepository<Usuario, Long> {
	/*
	Estos métodos tienen que servir para nuestra implementación personalizada de la interfaz
		UserDetailsManager de Spring Security:
	- findByUsername() -> loadUserByUsername() 
	- save(), que no hace falta declarar -> createUser(), updateUser() 
	- deleteByUsername() -> deleteUser() 
	- updatePassword() -> changePassword() 
	- existsByUsername() -> userExists()
	*/
	
	Usuario findByUsername(String username);
	
	void deleteByUsername(String username);
	
	@Modifying
    @Query("UPDATE User u SET u.password = ?1 WHERE u.username = ?2")
    void updatePassword(String username, String newPassword);
	
	boolean existsByUsername(String username);
	
}
