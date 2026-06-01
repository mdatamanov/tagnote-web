package com.tagnote.web.service;

import com.tagnote.web.entity.Note;
import com.tagnote.web.entity.Tag;
import com.tagnote.web.entity.User;
import com.tagnote.web.repository.NoteRepository;
import com.tagnote.web.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private NoteService noteService;

    private User testUser;
    private Note testNote;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("work");
        testTag.setOwner(testUser);

        testNote = new Note();
        testNote.setId(1L);
        testNote.setTitle("Test Note");
        testNote.setContent("Test Content");
        testNote.setOwner(testUser);
        testNote.setTags(new HashSet<>());
    }

    /**
     * Тест успешного создания заметки с тегами.
     * Проверяет, что при передаче корректных данных (заголовок, содержимое, теги)
     * заметка создаётся, теги привязываются (существующие или новые) и сохраняются в БД.
     * Ожидаемый результат: созданная заметка не null, заголовок совпадает с переданным,
     * метод save репозитория вызван ровно один раз.
     */
    @Test
    void createNote_ShouldCreateNote_WhenValidData() {
        when(tagRepository.findByNameAndOwner(anyString(), any(User.class))).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);

        Set<String> tagNames = Set.of("work");
        Note result = noteService.createNote(testUser, "Test Note", "Test Content", tagNames);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Note");
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    /**
     * Тест получения существующей заметки по ID.
     * Проверяет, что когда заметка существует и принадлежит текущему пользователю,
     * метод возвращает корректную заметку.
     * Ожидаемый результат: возвращённая заметка не null, её ID совпадает с запрошенным.
     */
    @Test
    void getNoteById_ShouldReturnNote_WhenExists() {
        when(noteRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(testNote));

        Note result = noteService.getNoteById(testUser, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    /**
     * Тест получения несуществующей заметки по ID.
     * Проверяет, что когда заметка не найдена в БД,
     * метод выбрасывает исключение с соответствующим сообщением.
     * Ожидаемый результат: исключение RuntimeException с сообщением "Note not found".
     */
    @Test
    void getNoteById_ShouldThrowException_WhenNotExists() {
        when(noteRepository.findByIdAndOwner(999L, testUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.getNoteById(testUser, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Note not found");
    }

    /**
     * Тест успешного удаления заметки.
     * Проверяет, что при удалении существующей заметки, принадлежащей пользователю,
     * метод deleteByIdAndOwner вызывается один раз.
     * Ожидаемый результат: метод delete вызван ровно один раз с правильными параметрами.
     */
    @Test
    void deleteNote_ShouldDelete_WhenExists() {
        when(noteRepository.existsByIdAndOwner(1L, testUser)).thenReturn(true);
        doNothing().when(noteRepository).deleteByIdAndOwner(1L, testUser);

        noteService.deleteNote(testUser, 1L);

        verify(noteRepository, times(1)).deleteByIdAndOwner(1L, testUser);
    }
}