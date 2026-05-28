package com.tagnote.web.repository;

import com.tagnote.web.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
    Метод для поиска по username
     */
    Optional<User> findByUsername(String username);

    /**
     * Метод для поиска по email
     */
    Optional<User> findByEmail(String email);

    /**
     * Метод для проверки существования по username
     */
    boolean existsByUsername(String username);

    /**
    * Метод для проверки существования по email
     */
    boolean existsByEmail(String email);

}
