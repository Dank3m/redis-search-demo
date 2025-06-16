package com.kinduberre.redissearchdemo.controller;

import com.kinduberre.redissearchdemo.model.CategoryStats;
import com.kinduberre.redissearchdemo.model.Page;
import com.kinduberre.redissearchdemo.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/")
public class SearchController {

    private final PostService postService;

    public SearchController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/search")
    public Page search (@RequestParam(name = "content", required = false) String content,
                        @RequestParam(name = "tags", required = false) Set<String> tags,
                        @RequestParam(name = "page", defaultValue = "1") Integer page) {
        return postService.search(content, tags, page);
    }

    @GetMapping("/categoryWisepost")
    public List<CategoryStats> getCategoryWiseTotalPosts() {
        return postService.getCategoryWiseTotalPosts();
    }

}
