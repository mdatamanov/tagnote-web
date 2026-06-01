package com.tagnote.web.repository;

import com.tagnote.web.entity.User;
import com.tagnote.web.entity.enums.ROLE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    /**
     * Создаём тестовые данные перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(ROLE.ROLE_USER);
        testUser.setEnabled(true);
        entityManager.persist(testUser);
        entityManager.flush();
    }

    /**
     * Тест: поиск пользователя по логину - успешный случай
     * Проверяет, что метод возвращает пользователя, если он существует
     */
    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        User found = userRepository.findByUsername("testuser").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("testuser");
        assertThat(found.getEmail()).isEqualTo("test@example.com");
    }

    /**
     * Тест: поиск пользователя по несуществующему логину - возвращает пустой Optional
     */
    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotExists() {
        User found = userRepository.findByUsername("nonexistent").orElse(null);

        assertThat(found).isNull();
    }

    /**
     * Тест: проверка существования логина - true когда существует
     */
    @Test
    void existsByUsername_ShouldReturnTrue_WhenExists() {
        boolean exists = userRepository.existsByUsername("testuser");

        assertThat(exists).isTrue();
    }

    /**
     * Тест: проверка существования email - true когда существует
     */
    @Test
    void existsByEmail_ShouldReturnTrue_WhenExists() {
        boolean exists = userRepository.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }
}