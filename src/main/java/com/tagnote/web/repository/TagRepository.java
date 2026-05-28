package com.tagnote.web.repository;

import com.tagnote.web.entities.Tag;
import com.tagnote.web.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Все теги пользователя
     * @param owner
     * @return список тегов пользователя
     */
    List<Tag> findByOwner(User owner);

    /**
     * Метод поиска тега по id and owner (для редактирования/удаления тега)
     * @param id
     * @param owner
     * @return
     */
    Optional<Tag> findByIdAndOwner(Long id, User owner);

    /**
     * Проверка существования по имени и владельцу (для уникальности тегов для каждого пользователя)
     * @param name
     * @param owner
     * @return
     */
    boolean existsByNameAndOwner(String name, User owner);

    Optional<Tag> findByNameAndOwner(String name, User owner);
    List<Tag> findByNameInAndOwner(Set<String> names, User owner);
}
