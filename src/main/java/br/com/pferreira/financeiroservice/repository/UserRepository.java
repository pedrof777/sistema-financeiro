package br.com.pferreira.financeiroservice.repository;

import br.com.pferreira.financeiroservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
