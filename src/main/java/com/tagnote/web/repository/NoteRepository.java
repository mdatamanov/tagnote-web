package com.tagnote.web.repository;

import com.tagnote.web.entities.Note;
import com.tagnote.web.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Получить все заметки пользователя (с пагинацией и сортировкой)
     */
    Page<Note> findByOwner(User owner, Pageable pageable);

    /**
     * Получить заметку по id с проверкой пользователя
     */
    Optional<Note> findByIdAndOwner(Long id, User owner);

    /**
     * Удалить заметку по id с проверкой пользователя
     */
    void deleteByIdAndOwner(Long id, User owner);
}
