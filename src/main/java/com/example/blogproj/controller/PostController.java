package com.example.blogproj.controller;

import com.example.blogproj.entity.Comment;
import com.example.blogproj.entity.Post;
import com.example.blogproj.entity.Tag;
import com.example.blogproj.service.PostService;
import com.example.blogproj.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private TagService tagService;

    @Autowired
    private PostService postService;


    @PostMapping

    public ResponseEntity<Post> createPost(
            @RequestParam("title") String title,
            @RequestParam("tags") String tags,
            @RequestParam("excerpt") String excerpt,
            @RequestParam("content") String content,
            @RequestParam("author") String author,
            @RequestParam(value = "isPublished", required = false, defaultValue = "false") boolean isPublished) {

        String[] tagArray = tags.split(",");
        List<Tag> tagList = new ArrayList<>();
        for (String tagName : tagArray) {
            tagName = tagName.trim();
            Tag tag = tagService.findOrCreateByName(tagName);
            tagList.add(tag);
        }

        Post post = new Post();
        post.setTitle(title);
        post.setTags(tagList);
        post.setExcerpt(excerpt);
        post.setContent(content);
        post.setAuthor(author);
        post.setIsPublished(isPublished);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postService.savePost(post);

        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Post>> listPosts(
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "limit", defaultValue = "2") int limit,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "authors", required = false) List<String> authors) {

        int page = (start - 1) / limit;
        Pageable pageable = PageRequest.of(page, limit);
        Page<Post> postsPage;

        //if ((tags == null || tags.isEmpty()) && (authors == null || authors.isEmpty())) {
        postsPage = postService.getPosts(page, limit);
        //  } //else {
        //   postsPage = postService.filterPostsByTagsAndAuthors(tags, authors, pageable);
        //  }

        return new ResponseEntity<>(postsPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> viewPost(@PathVariable Long id) {
        System.out.println("Fetching post with id: " + id);
        Optional<Post> post = postService.getPostById(id);
        System.out.println("Post found: " + post.isPresent());
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("tags") String tags,
            @RequestParam("excerpt") String excerpt,
            @RequestParam("content") String content,
            @RequestParam("author") String author,
            @RequestParam(value = "isPublished", required = false, defaultValue = "false") boolean isPublished) {

        String[] tagArray = tags.split(",");
        List<Tag> tagList = new ArrayList<>();
        for (String tagName : tagArray) {
            tagName = tagName.trim();
            Tag tag = tagService.findOrCreateByName(tagName);
            tagList.add(tag);
        }

        Optional<Post> existingPost = postService.getPostById(id);
        if (existingPost.isPresent()) {
            Post post = existingPost.get();
            post.setTitle(title);
            post.setTags(tagList);
            post.setExcerpt(excerpt);
            post.setContent(content);
            post.setAuthor(author);
            post.setIsPublished(isPublished);
            post.setUpdatedAt(LocalDateTime.now());

            postService.savePost(post);

            return new ResponseEntity<>(post, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (postService.getPostById(id).isPresent()) {
            postService.deletePost(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<Post>> viewSortedPosts(
            @RequestParam(name = "sort") String sortOrder) {
        List<Post> posts;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            posts = postService.getAllPostsSortedByDateAsc();
        } else {
            posts = postService.getAllPostsSortedByDateDesc();
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam("query") String query) {
        List<Post> searchResults = postService.searchPosts(query);
        return new ResponseEntity<>(searchResults, HttpStatus.OK);
    }
}
//    @GetMapping("/filter")
//    public ResponseEntity<Page<Post>> filterPosts(
//            @RequestParam(value = "tags", required = false) List<String> tags,
//            @RequestParam(value = "authors", required = false) List<String> authors,
//            @RequestParam(name = "start", defaultValue = "0") int start,
//            @RequestParam(name = "limit", defaultValue = "10") int limit
//    ) {
//        Pageable pageable = PageRequest.of(start, limit);
//        Page<Post> filteredPosts = postService.filterPostsByTagsAndAuthors(tags, authors, pageable);
//
//        if (filteredPosts.hasContent()) {
//            return new ResponseEntity<>(filteredPosts, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//    }
//}


