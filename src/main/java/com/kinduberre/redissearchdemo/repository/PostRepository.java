package com.kinduberre.redissearchdemo.repository;

import com.google.gson.Gson;
import com.kinduberre.redissearchdemo.model.Page;
import com.kinduberre.redissearchdemo.model.Post;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PostRepository {

    private final UnifiedJedis jedis;

    private static final Integer PAGE_SIZE = 5;

    public PostRepository(UnifiedJedis jedis) {
        this.jedis = jedis;
    }

    public Post save(Post post) {
        if(post.getPostId() == null) {
            post.setPostId(UUID.randomUUID().toString());
        }

        Gson gson = new Gson();
        String key = "post:" + post.getPostId();
        jedis.jsonSet(key, gson.toJson(post));
        jedis.sadd("post", key);

        return post;
    }

    public void deleteAll() {
        Set<String> keys = jedis.smembers("post");
        if(!keys.isEmpty()) {
            keys.forEach(jedis::jsonDel);
        }
        jedis.del("post");
    }

    public Page search(String content, Set<String> tags, Integer page) {
        Long totalResults = 0L;

        StringBuilder queryBuilder = new StringBuilder();

        if (content != null && !content.isEmpty()) {
            queryBuilder.append("@content:" + content);
        }

        if (tags != null && !tags.isEmpty()) {
            queryBuilder.append(" ").append(" @tags:{" + tags.stream().collect(Collectors.joining("|")) + "}");
        }

        String queryCriteria = queryBuilder.toString();
        Query query = null;

        if (queryCriteria.isEmpty()) {
            query = new Query();
        } else {
            query = new Query(queryCriteria);
        }

        query.limit(PAGE_SIZE * ( page -1), PAGE_SIZE);
        SearchResult searchResult = jedis.ftSearch("post-idx", query);
        totalResults = searchResult.getTotalResults();

        int numberOfPages = (int) Math.ceil((double) totalResults / PAGE_SIZE);

        List<Post> posts = searchResult.getDocuments()
                .stream()
                .map(this::convertDocumentToPost)
                .collect(Collectors.toList());

        return Page.builder()
                .posts(posts)
                .total(totalResults)
                .totalPages(numberOfPages)
                .currentPage(page)
                .build();
    }

    private Post convertDocumentToPost(Document document) {
        Gson gson = new Gson();
        String jsonDoc = document
                .getProperties()
                .iterator()
                .next()
                .getValue()
                .toString();
        return gson.fromJson(jsonDoc, Post.class);
    }
}
