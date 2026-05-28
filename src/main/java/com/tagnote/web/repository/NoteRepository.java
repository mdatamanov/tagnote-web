package com.tagnote.web.repository;

import com.tagnote.web.entity.Note;
import com.tagnote.web.entity.Tag;
import com.tagnote.web.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findByOwner(User owner, Pageable pageable);

    Optional<Note> findByIdAndOwner(Long id, User owner);

    void deleteByIdAndOwner(Long id, User owner);

    boolean existsByIdAndOwner(Long id, User owner);

    @Query("SELECT DISTINCT n FROM Note n LEFT JOIN n.tags t WHERE n.owner = :owner AND t IN :tags")
    Page<Note> findByOwnerAndTagsIn(@Param("owner") User owner,
                                    @Param("tags") Set<Tag> tags,
                                    Pageable pageable);

    @Query("SELECT DISTINCT n FROM Note n JOIN n.tags t WHERE n.owner = :owner AND t = :tag")
    Page<Note> findByOwnerAndTag(@Param("owner") User owner,
                                 @Param("tag") Tag tag,
                                 Pageable pageable);

    @Query("SELECT n FROM Note n WHERE n.owner = :owner AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Note> searchByTitleOrContent(@Param("owner") User owner,
                                      @Param("searchTerm") String searchTerm,
                                      Pageable pageable);
}
