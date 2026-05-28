package com.tagnote.web.service;

import com.tagnote.web.entities.Note;
import com.tagnote.web.entities.Tag;
import com.tagnote.web.entities.User;
import com.tagnote.web.repository.NoteRepository;
import com.tagnote.web.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    public Note createNote(User owner, String title, String content, Set<String> tagNames) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setOwner(owner);

        if (tagNames != null && !tagNames.isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByNameAndOwner(tagName, owner)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            newTag.setOwner(owner);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
            note.setTags(tags);
        }

        return noteRepository.save(note);
    }

    public Page<Note> getUserNotes(User owner, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return noteRepository.findByOwner(owner, pageable);
    }

    public Note getNoteById(User owner, Long noteId) {
        return noteRepository.findByIdAndOwner(noteId, owner)
                .orElseThrow(() -> new RuntimeException("Note not found"));
    }

    @Transactional
    public Note updateNote(User owner, Long noteId, String title, String content, Set<String> tagNames) {
        Note note = noteRepository.findByIdAndOwner(noteId, owner)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        note.setTitle(title);
        note.setContent(content);

        if (tagNames != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByNameAndOwner(tagName, owner)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            newTag.setOwner(owner);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
            note.setTags(tags);
        }

        return noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(User owner, Long noteId) {
        if (!noteRepository.existsByIdAndOwner(noteId, owner)) {
            throw new RuntimeException("Note not found");
        }
        noteRepository.deleteByIdAndOwner(noteId, owner);
    }

    public Page<Note> filterByTags(User owner, Set<String> tagNames, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Set<Tag> tags = new HashSet<>(tagRepository.findByNameInAndOwner(tagNames, owner));

        if (tags.isEmpty()) {
            return Page.empty();
        }

        return noteRepository.findByOwnerAndTagsIn(owner, tags, pageable);
    }

    public Page<Note> searchNotes(User owner, String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return noteRepository.searchByTitleOrContent(owner, searchTerm, pageable);
    }
}
