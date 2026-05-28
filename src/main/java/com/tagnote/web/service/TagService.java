package com.tagnote.web.service;

import com.tagnote.web.entity.Tag;
import com.tagnote.web.entity.User;
import com.tagnote.web.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private static final Logger log = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;

    @Transactional
    public Tag createTag(User owner, String name) {
        log.info("Создание тега '{}' пользователем {}", name, owner.getUsername());

        if (tagRepository.existsByNameAndOwner(name, owner)) {
            log.warn("Тег '{}' уже существует у пользователя {}", name, owner.getUsername());
            throw new RuntimeException("Tag already exists");
        }

        Tag tag = new Tag();
        tag.setName(name);
        tag.setOwner(owner);

        Tag savedTag = tagRepository.save(tag);
        log.info("Тег создан: ID={}, имя='{}'", savedTag.getId(), name);

        return savedTag;
    }

    public List<Tag> getUserTags(User owner) {
        log.debug("Получение всех тегов пользователя {}", owner.getUsername());
        return tagRepository.findByOwner(owner);
    }

    @Transactional
    public Tag renameTag(User owner, Long tagId, String newName) {
        log.info("Переименование тега ID={} пользователем {} в '{}'", tagId, owner.getUsername(), newName);

        Tag tag = tagRepository.findByIdAndOwner(tagId, owner)
                .orElseThrow(() -> {
                    log.warn("Тег не найден: ID={}", tagId);
                    return new RuntimeException("Tag not found");
                });

        String oldName = tag.getName();
        tag.setName(newName);

        Tag savedTag = tagRepository.save(tag);
        log.info("Тег переименован: '{}' -> '{}'", oldName, newName);

        return savedTag;
    }

    @Transactional
    public void deleteTag(User owner, Long tagId) {
        log.info("Удаление тега ID={} пользователем {}", tagId, owner.getUsername());

        Tag tag = tagRepository.findByIdAndOwner(tagId, owner)
                .orElseThrow(() -> {
                    log.warn("Тег не найден для удаления: ID={}", tagId);
                    return new RuntimeException("Tag not found");
                });

        String tagName = tag.getName();
        tagRepository.delete(tag);
        log.info("Тег удалён: ID={}, имя='{}'", tagId, tagName);
    }
}