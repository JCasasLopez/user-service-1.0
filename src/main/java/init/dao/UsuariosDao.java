package init.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import init.entities.Usuario;

public interface UsuariosDao extends JpaRepository<Usuario, Long> {
	
	Usuario findByUsername(String username);
	
	void deleteByUsername(String username);
	
	@Modifying
    @Query("UPDATE User u SET u.password = ?1 WHERE u.username = ?2")
    void updatePassword(String username, String newPassword);
	
	boolean existsByUsername(String username);
	
}
