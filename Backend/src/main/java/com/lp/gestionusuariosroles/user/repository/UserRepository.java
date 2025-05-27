package com.lp.gestionusuariosroles.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByDniAndIdNot(String dni, Long id);
}
