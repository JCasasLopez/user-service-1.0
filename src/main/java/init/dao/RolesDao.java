package init.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import init.entities.Rol;

public interface RolesDao extends JpaRepository<Rol, Integer> {

}
