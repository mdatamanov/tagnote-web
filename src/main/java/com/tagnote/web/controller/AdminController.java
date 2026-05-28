package com.tagnote.web.controller;

import com.tagnote.web.entities.Note;
import com.tagnote.web.entities.User;
import com.tagnote.web.repository.NoteRepository;
import com.tagnote.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<User> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    @PutMapping("/users/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> toggleUserBlock(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean enabled = request.get("enabled");
        user.setEnabled(enabled);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", enabled ? "User unblocked" : "User blocked");
        response.put("userId", user.getId());
        response.put("enabled", user.isEnabled());

        return response;
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "User deleted successfully");

        return response;
    }

    @GetMapping("/notes")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Note> getAllNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return noteRepository.findAll(PageRequest.of(page, size));
    }

    @DeleteMapping("/notes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> deleteAnyNote(@PathVariable Long id) {
        noteRepository.deleteById(id);

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Note deleted successfully by admin");

        return response;
    }
}