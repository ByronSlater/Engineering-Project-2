package com.makersacademy.acebook.service;

import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.repository.CommentRepository;
import com.makersacademy.acebook.repository.PostRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PostServiceTest {

    @Test
    public void returnsOldestPostsFirstWhenOldestSelected() {
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

        Post olderPost = new Post("Older post");
        Post newerPost = new Post("Newer post");

        when(postRepository.findAllByOrderByCreatedAtAsc())
                .thenReturn(List.of(olderPost, newerPost));

        PostService postService =
                new PostService(postRepository, commentRepository);

        List<Post> posts = postService.allPosts("oldest", ""); // the empty string means do not search, just give me all posts oldest first

        assertEquals(olderPost, posts.get(0));
        assertEquals(newerPost, posts.get(1));
    }

    @Test
    public void searchesPostsByKeywordNewestFirst() {
        PostRepository postRepository = Mockito.mock(PostRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

        Post matchingPost = new Post("I am learning Java");

        when(postRepository
                .findByContentContainingIgnoreCaseOrderByCreatedAtDesc("java"))
                .thenReturn(List.of(matchingPost));

        PostService postService =
                new PostService(postRepository, commentRepository);

        List<Post> posts =
                postService.allPosts("newest", "java");

        assertEquals(1, posts.size());
        assertEquals(matchingPost, posts.get(0));
    }
}
