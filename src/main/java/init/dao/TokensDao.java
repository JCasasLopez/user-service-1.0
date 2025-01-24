package init.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import init.entities.TokenJwt;

public interface TokensDao extends JpaRepository<TokenJwt, Long> {
	TokenJwt findByToken(String token);
}
