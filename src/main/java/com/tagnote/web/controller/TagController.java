package com.tagnote.web.controller;

import com.tagnote.web.entities.Tag;
import com.tagnote.web.entities.User;
import com.tagnote.web.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    public Map<String, Object> createTag(@AuthenticationPrincipal User currentUser, @RequestBody Map<String, String> request) {

        String name = request.get("name");
        Tag tag = tagService.createTag(currentUser, name);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Tag created successfully");
        response.put("tagId", tag.getId());
        response.put("name", tag.getName());

        return response;
    }

    @GetMapping
    public List<Tag> getUserTags(@AuthenticationPrincipal User currentUser) {
        return tagService.getUserTags(currentUser);
    }

    @PutMapping("/{id}")
    public Map<String, Object> renameTag(@AuthenticationPrincipal User currentUser, @PathVariable Long id, @RequestBody Map<String, String> request) {

        String newName = request.get("name");
        Tag tag = tagService.renameTag(currentUser, id, newName);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Tag renamed successfully");
        response.put("tagId", tag.getId());
        response.put("name", tag.getName());

        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteTag(@AuthenticationPrincipal User currentUser, @PathVariable Long id) {

        tagService.deleteTag(currentUser, id);

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Tag deleted successfully");

        return response;
    }
}