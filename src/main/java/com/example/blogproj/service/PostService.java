package com.example.blogproj.service;

import com.example.blogproj.entity.Post;
import com.example.blogproj.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPostsSortedByDateAsc() {
        return postRepository.findAllByOrderByPublishedAtAsc();
    }

    public List<Post> getAllPostsSortedByDateDesc() {
        return postRepository.findAllByOrderByPublishedAtDesc();
    }

    public Post savePost(Post post) {
        postRepository.save(post);
        return post;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public void updatePostById(Long id, Post updatedPost) {
        Optional<Post> existingPostOptional = postRepository.findById(id);
        if (existingPostOptional.isPresent()) {
            Post existingPost = existingPostOptional.get();
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setContent(updatedPost.getContent());
            existingPost.setAuthor(updatedPost.getAuthor());
            existingPost.setUpdatedAt(updatedPost.getUpdatedAt());
            postRepository.save(existingPost);
        }
    }

    public void deletePost(Long id) {

        postRepository.deleteById(id);
    }




    public List<Post> searchPosts(String keyword) {
        return postRepository.searchPosts(keyword);
    }


   public Page<Post> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAll(pageable);
    }





    public List<String> getDistinctAuthors() {
        return postRepository.findDistinctAuthors();
    }








   // public Page<Post> getFilteredPosts(List<String> tags, List<String> authors, Pageable pageable) {
  //  }
}




