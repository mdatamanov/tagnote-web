package com.tagnote.web.repository;

import com.tagnote.web.entity.Note;
import com.tagnote.web.entity.Tag;
import com.tagnote.web.entity.User;
import com.tagnote.web.entity.enums.ROLE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест для NoteRepository
 */
@DataJpaTest
@ActiveProfiles("test")
class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private Note testNote;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        // Создаём пользователя
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(ROLE.ROLE_USER);
        testUser.setEnabled(true);
        entityManager.persist(testUser);

        // Создаём тег
        testTag = new Tag();
        testTag.setName("work");
        testTag.setOwner(testUser);
        entityManager.persist(testTag);

        // Создаём заметку с тегом
        testNote = new Note();
        testNote.setTitle("Test Note");
        testNote.setContent("Test Content");
        testNote.setOwner(testUser);
        testNote.setTags(Set.of(testTag));
        entityManager.persist(testNote);

        entityManager.flush();
    }

    /**
     * Тест: поиск заметок по владельцу с пагинацией
     * Проверяет, что возвращается правильная страница заметок
     */
    @Test
    void findByOwner_ShouldReturnNotes() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Note> result = noteRepository.findByOwner(testUser, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Note");
    }

    /**
     * Тест: поиск заметки по ID и владельцу
     */
    @Test
    void findByIdAndOwner_ShouldReturnNote_WhenExists() {
        Note result = noteRepository.findByIdAndOwner(testNote.getId(), testUser).orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Note");
    }

    /**
     * Тест: проверка существования заметки
     */
    @Test
    void existsByIdAndOwner_ShouldReturnTrue_WhenExists() {
        boolean exists = noteRepository.existsByIdAndOwner(testNote.getId(), testUser);

        assertThat(exists).isTrue();
    }

    /**
     * Тест: удаление заметки по ID и владельцу
     */
    @Test
    void deleteByIdAndOwner_ShouldDelete_WhenExists() {
        noteRepository.deleteByIdAndOwner(testNote.getId(), testUser);

        boolean exists = noteRepository.existsByIdAndOwner(testNote.getId(), testUser);
        assertThat(exists).isFalse();
    }

    /**
     * Тест: поиск заметок по подстроке в заголовке или содержании
     */
    @Test
    void searchByTitleOrContent_ShouldFindNote() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Note> result = noteRepository.searchByTitleOrContent(testUser, "Test", pageable);

        assertThat(result.getContent()).hasSize(1);
    }
}