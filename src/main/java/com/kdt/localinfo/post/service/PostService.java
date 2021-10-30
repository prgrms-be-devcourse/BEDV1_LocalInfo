package com.kdt.localinfo.post.service;

import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.converter.PostConverter;
import com.kdt.localinfo.post.dto.PostDto;
import com.kdt.localinfo.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final PostConverter postConverter;

    public PostService(PostRepository postRepository, PostConverter postConverter) {
        this.postRepository = postRepository;
        this.postConverter = postConverter;
    }

    @Transactional
    public Long createPost(PostDto postDto) {
        Post post = postConverter.convertToPost(postDto);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }
}
