package com.tagnote.web;

import com.tagnote.web.entities.User;
import com.tagnote.web.entities.enums.ROLE;
import com.tagnote.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class TagnoteWebApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(TagnoteWebApplication.class, args);
    }
    @Bean
    public CommandLineRunner createAdminUser() {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@tagnote.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(ROLE.ROLE_ADMIN);
                admin.setEnabled(true);

                userRepository.save(admin);
                System.out.println("========================================");
                System.out.println("✅ ADMIN USER CREATED SUCCESSFULLY!");
                System.out.println("   Username: admin");
                System.out.println("   Password: admin123");
                System.out.println("========================================");
            } else {
                System.out.println("ℹ️ Admin user already exists");
            }
        };
    }
}
