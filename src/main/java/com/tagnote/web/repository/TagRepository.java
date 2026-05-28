package com.tagnote.web.repository;

import com.tagnote.web.entity.Tag;
import com.tagnote.web.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByOwner(User owner);

    Optional<Tag> findByIdAndOwner(Long id, User owner);

    boolean existsByNameAndOwner(String name, User owner);

    Optional<Tag> findByNameAndOwner(String name, User owner);
    List<Tag> findByNameInAndOwner(Set<String> names, User owner);
}
