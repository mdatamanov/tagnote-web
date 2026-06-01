package com.tagnote.web.service;

import com.tagnote.web.entity.User;
import com.tagnote.web.entity.enums.ROLE;
import com.tagnote.web.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(ROLE.ROLE_USER);
        testUser.setEnabled(true);
    }

    /**
     * Тест успешной регистрации пользователя.
     * Проверяет, что при валидных данных (уникальные логин и email, корректный пароль)
     * пользователь создаётся и сохраняется в БД.
     * Ожидаемый результат: метод возвращает созданного пользователя с правильным username.
     */
    @Test
    void register_ShouldCreateUser_WhenValidData() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.register("testuser", "test@example.com", "password123");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Тест регистрации с уже существующим логином.
     * Проверяет, что при попытке зарегистрировать пользователя с логином,
     * который уже есть в БД, выбрасывается исключение.
     * Ожидаемый результат: исключение RuntimeException с сообщением о существующем логине.
     */
    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("testuser", "test@example.com", "password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Пользователь с таким логином уже существует");
    }

    /**
     * Тест регистрации с уже существующим email.
     * Проверяет, что при попытке зарегистрировать пользователя с email,
     * который уже есть в БД, выбрасывается исключение.
     * Ожидаемый результат: исключение RuntimeException с сообщением о существующем email.
     */
    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("testuser", "test@example.com", "password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Пользователь с таким email уже зарегистрирован");
    }

    /**
     * Тест поиска пользователя по логину, когда пользователь существует.
     * Проверяет, что метод findByUsername возвращает корректного пользователя,
     * если он найден в БД.
     * Ожидаемый результат: возвращённый пользователь не null и имеет правильный username.
     */
    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.findByUsername("testuser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    /**
     * Тест поиска пользователя по логину, когда пользователь не существует.
     * Проверяет, что при попытке найти несуществующего пользователя
     * выбрасывается исключение.
     * Ожидаемый результат: исключение RuntimeException.
     */
    @Test
    void findByUsername_ShouldThrowException_WhenNotExists() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("unknown"))
                .isInstanceOf(RuntimeException.class);
    }
}