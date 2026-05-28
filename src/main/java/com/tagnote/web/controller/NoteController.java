package com.tagnote.web.controller;

import com.tagnote.web.entity.Note;
import com.tagnote.web.entity.User;
import com.tagnote.web.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public Map<String, Object> createNote(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Map<String, Object> request) {

        String title = (String) request.get("title");
        String content = (String) request.get("content");

        // Преобразуем List в Set
        Set<String> tagNames = new HashSet<>();
        Object tagsObj = request.get("tags");
        if (tagsObj instanceof List) {
            List<?> tagsList = (List<?>) tagsObj;
            for (Object tag : tagsList) {
                if (tag instanceof String) {
                    tagNames.add((String) tag);
                }
            }
        }

        Note note = noteService.createNote(currentUser, title, content, tagNames);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Note created successfully");
        response.put("noteId", note.getId());
        response.put("title", note.getTitle());
        response.put("content", note.getContent());
        response.put("createdAt", note.getCreatedAt());

        return response;
    }

    @GetMapping
    public Page<Note> getUserNotes(@AuthenticationPrincipal User currentUser, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        return noteService.getUserNotes(currentUser, page, size);
    }

    @GetMapping("/{id}")
    public Note getNoteById(@AuthenticationPrincipal User currentUser, @PathVariable Long id) {

        return noteService.getNoteById(currentUser, id);
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateNote(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {

        String title = (String) request.get("title");
        String content = (String) request.get("content");

        Set<String> tagNames = new HashSet<>();
        Object tagsObj = request.get("tags");
        if (tagsObj instanceof List) {
            List<?> tagsList = (List<?>) tagsObj;
            for (Object tag : tagsList) {
                if (tag instanceof String) {
                    tagNames.add((String) tag);
                }
            }
        }

        Note note = noteService.updateNote(currentUser, id, title, content, tagNames);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Note updated successfully");
        response.put("noteId", note.getId());
        response.put("title", note.getTitle());
        response.put("content", note.getContent());
        response.put("updatedAt", note.getUpdatedAt());

        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteNote(@AuthenticationPrincipal User currentUser, @PathVariable Long id) {

        noteService.deleteNote(currentUser, id);

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Note deleted successfully");

        return response;
    }

    @GetMapping("/filter")
    public Page<Note> filterByTags(@AuthenticationPrincipal User currentUser, @RequestParam Set<String> tags, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        return noteService.filterByTags(currentUser, tags, page, size);
    }

    @GetMapping("/search")
    public Page<Note> searchNotes(@AuthenticationPrincipal User currentUser, @RequestParam String term, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        return noteService.searchNotes(currentUser, term, page, size);
    }
}