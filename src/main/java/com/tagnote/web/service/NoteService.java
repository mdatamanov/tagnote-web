package com.tagnote.web.service;

import com.tagnote.web.entity.Note;
import com.tagnote.web.entity.Tag;
import com.tagnote.web.entity.User;
import com.tagnote.web.repository.NoteRepository;
import com.tagnote.web.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(NoteService.class);

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Note createNote(User owner, String title, String content, Set<String> tagNames) {
        log.info("Создание заметки пользователем: {} - '{}'", owner.getUsername(), title);

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setOwner(owner);

        if (tagNames != null && !tagNames.isEmpty()) {
            log.debug("Добавление тегов к заметке: {}", tagNames);
            Set<Tag> tags = new HashSet<>();
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByNameAndOwner(tagName, owner)
                        .orElseGet(() -> {
                            log.debug("Создание нового тега: {}", tagName);
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            newTag.setOwner(owner);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
            note.setTags(tags);
        }

        Note savedNote = noteRepository.save(note);
        log.info("Заметка создана: ID={}, пользователь={}", savedNote.getId(), owner.getUsername());

        return savedNote;
    }

    public Page<Note> getUserNotes(User owner, int page, int size) {
        log.debug("Получение заметок пользователя: {}, страница={}, размер={}", owner.getUsername(), page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return noteRepository.findByOwner(owner, pageable);
    }

    public Note getNoteById(User owner, Long noteId) {
        log.debug("Получение заметки ID={} пользователем {}", noteId, owner.getUsername());
        return noteRepository.findByIdAndOwner(noteId, owner)
                .orElseThrow(() -> {
                    log.warn("Заметка не найдена: ID={}, пользователь={}", noteId, owner.getUsername());
                    return new RuntimeException("Note not found");
                });
    }

    @Transactional
    public Note updateNote(User owner, Long noteId, String title, String content, Set<String> tagNames) {
        log.info("Обновление заметки ID={} пользователем {}", noteId, owner.getUsername());

        Note note = noteRepository.findByIdAndOwner(noteId, owner)
                .orElseThrow(() -> {
                    log.warn("Заметка не найдена для обновления: ID={}", noteId);
                    return new RuntimeException("Note not found");
                });

        note.setTitle(title);
        note.setContent(content);

        if (tagNames != null) {
            log.debug("Обновление тегов заметки: {}", tagNames);
            Set<Tag> tags = new HashSet<>();
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByNameAndOwner(tagName, owner)
                        .orElseGet(() -> {
                            log.debug("Создание нового тега при обновлении: {}", tagName);
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            newTag.setOwner(owner);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
            note.setTags(tags);
        }

        Note updatedNote = noteRepository.save(note);
        log.info("Заметка обновлена: ID={}", noteId);

        return updatedNote;
    }

    @Transactional
    public void deleteNote(User owner, Long noteId) {
        log.info("Удаление заметки ID={} пользователем {}", noteId, owner.getUsername());

        if (!noteRepository.existsByIdAndOwner(noteId, owner)) {
            log.warn("Заметка не найдена для удаления: ID={}", noteId);
            throw new RuntimeException("Note not found");
        }

        noteRepository.deleteByIdAndOwner(noteId, owner);
        log.info("Заметка удалена: ID={}", noteId);
    }

    public Page<Note> filterByTags(User owner, Set<String> tagNames, int page, int size) {
        log.debug("Фильтрация заметок по тегам: {} (пользователь {})", tagNames, owner.getUsername());
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Set<Tag> tags = new HashSet<>(tagRepository.findByNameInAndOwner(tagNames, owner));

        if (tags.isEmpty()) {
            log.debug("Теги не найдены, возвращена пустая страница");
            return Page.empty();
        }

        return noteRepository.findByOwnerAndTagsIn(owner, tags, pageable);
    }

    public Page<Note> searchNotes(User owner, String searchTerm, int page, int size) {
        log.debug("Поиск заметок по тексту: '{}' (пользователь {})", searchTerm, owner.getUsername());
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return noteRepository.searchByTitleOrContent(owner, searchTerm, pageable);
    }
}