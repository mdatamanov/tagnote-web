package com.tagnote.web.service;

import com.tagnote.web.entities.Tag;
import com.tagnote.web.entities.User;
import com.tagnote.web.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public Tag createTag(User owner, String name) {
        if(tagRepository.existsByNameAndOwner(name, owner)){
            throw new RuntimeException("Tag already exists");
        }
        Tag tag = new Tag();
        tag.setOwner(owner);
        tag.setName(name);
        return tagRepository.save(tag);
    }

    public List<Tag> getUserTags(User owner) {
        return tagRepository.findByOwner(owner);
    }

    @Transactional
    public Tag renameTag(User owner, Long tagId, String newName) {
        Tag tag = tagRepository.findByIdAndOwner(tagId, owner).orElseThrow(() -> new RuntimeException("tag not found"));
        tag.setName(newName);
        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(User owner, Long tagId) {
        Tag tag =  tagRepository.findByIdAndOwner(tagId, owner).orElseThrow(() -> new RuntimeException("tag not found"));
        tagRepository.delete(tag);
    }
}
