package com.tagnote.web.service;

import com.tagnote.web.entity.User;
import com.tagnote.web.entity.enums.ROLE;
import com.tagnote.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String username, String email, String password) {
        log.info("Попытка регистрации нового пользователя: {}", username);

        if (userRepository.existsByUsername(username)) {
            log.warn("Регистрация отклонена:用户名 {} уже существует", username);
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }
        if (userRepository.existsByEmail(email)) {
            log.warn("Регистрация отклонена: email {} уже зарегистрирован", email);
            throw new RuntimeException("Пользователь с таким email уже зарегистрирован");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(ROLE.ROLE_USER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        log.info("Пользователь успешно зарегистрирован: {} (ID: {})", username, savedUser.getId());

        return savedUser;
    }

    public User findByUsername(String username) {
        log.debug("Поиск пользователя по имени: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден: {}", username);
                    return new RuntimeException("User not found: " + username);
                });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Загрузка пользователя для аутентификации: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден при аутентификации: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
    }
}
