package com.example.blogproj.service;

import com.example.blogproj.entity.Tag;
import com.example.blogproj.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public Tag findOrCreateByName(String name) {

        Optional<Tag> existingTag = tagRepository.findByName(name);
        if (existingTag.isPresent()) {
            return existingTag.get();
        }

        Tag newTag = new Tag();
        newTag.setName(name);
        return tagRepository.save(newTag);
    }

    public List<Tag> findAllTags() {
        return tagRepository.findAll();
    }

    public List<Tag> findTagsByIds(List<Long> tagsList){
        List<Tag> tags = new ArrayList<>();

        for (Long tagId : tagsList) {
            Optional<Tag> optionalTag = tagRepository.findById(tagId);
            if (optionalTag.isPresent()) {
                tags.add(optionalTag.get());
            }
        }
        return tags;
    }
}
