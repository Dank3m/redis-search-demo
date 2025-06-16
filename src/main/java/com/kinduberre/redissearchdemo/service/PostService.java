package com.kinduberre.redissearchdemo.service;

import com.kinduberre.redissearchdemo.model.Page;
import com.kinduberre.redissearchdemo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Page search(String content, Set<String> tags, Integer page) {
        return postRepository.search(content, tags, page);
    }
}
