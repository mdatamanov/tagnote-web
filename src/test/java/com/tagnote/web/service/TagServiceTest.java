package com.tagnote.web.service;

import com.tagnote.web.entity.Tag;
import com.tagnote.web.entity.User;
import com.tagnote.web.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private User testUser;
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
    }

    /**
     * Тест успешного создания нового тега.
     * Проверяет, что при создании тега с уникальным именем
     * тег сохраняется в БД и возвращается с правильным именем.
     * Ожидаемый результат: созданный тег не null, имя совпадает с переданным.
     */
    @Test
    void createTag_ShouldCreateTag_WhenValid() {
        when(tagRepository.existsByNameAndOwner("work", testUser)).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        Tag result = tagService.createTag(testUser, "work");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("work");
    }

    /**
     * Тест создания дубликата тега.
     * Проверяет, что при попытке создать тег с именем,
     * которое уже существует у данного пользователя, выбрасывается исключение.
     * Ожидаемый результат: исключение RuntimeException с сообщением о существующем теге.
     */
    @Test
    void createTag_ShouldThrowException_WhenExists() {
        when(tagRepository.existsByNameAndOwner("work", testUser)).thenReturn(true);

        assertThatThrownBy(() -> tagService.createTag(testUser, "work"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tag already exists");
    }

    /**
     * Тест получения всех тегов пользователя.
     * Проверяет, что метод возвращает список всех тегов,
     * принадлежащих текущему пользователю.
     * Ожидаемый результат: список содержит 1 элемент с правильным именем тега.
     */
    @Test
    void getUserTags_ShouldReturnList() {
        when(tagRepository.findByOwner(testUser)).thenReturn(List.of(testTag));

        List<Tag> result = tagService.getUserTags(testUser);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("work");
    }

    /**
     * Тест переименования существующего тега.
     * Проверяет, что при переименовании тега, который существует у пользователя,
     * имя тега успешно обновляется.
     * Ожидаемый результат: возвращённый тег имеет новое имя "important".
     */
    @Test
    void renameTag_ShouldRename_WhenExists() {
        when(tagRepository.findByIdAndOwner(1L, testUser)).thenReturn(Optional.of(testTag));
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        Tag result = tagService.renameTag(testUser, 1L, "important");

        assertThat(result.getName()).isEqualTo("important");
    }
}