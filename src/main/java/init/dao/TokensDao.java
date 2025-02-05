package init.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import init.entities.TokenJwt;

public interface TokensDao extends JpaRepository<TokenJwt, Integer> {
	
	@Query("SELECT t FROM TokenJwt t WHERE t.token = ?1")
	Optional<TokenJwt> findByToken(String token);
}
