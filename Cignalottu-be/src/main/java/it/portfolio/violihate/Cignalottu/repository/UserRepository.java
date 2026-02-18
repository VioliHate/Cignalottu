package it.portfolio.violihate.cignalottu.repository;

import it.portfolio.violihate.cignalottu.entity.Role;
import it.portfolio.violihate.cignalottu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String mail);
    List<User> findByRole(Role role);
}
