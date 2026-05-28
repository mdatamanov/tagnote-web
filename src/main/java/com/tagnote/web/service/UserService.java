package com.tagnote.web.service;

import com.tagnote.web.entities.User;
import com.tagnote.web.entities.enums.ROLE;
import com.tagnote.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String username, String email, String password){
        if(userRepository.existsByUsername(username)){
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }
        if(userRepository.existsByEmail(email)){
            throw new RuntimeException("Пользователь с таким email уже зарегистрирован");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(ROLE.ROLE_USER);
        return userRepository.save(user);
    }
}
