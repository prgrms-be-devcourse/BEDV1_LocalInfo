package com.kdt.localinfo.post.service;

import com.kdt.localinfo.post.Entity.Post;
import com.kdt.localinfo.post.converter.PostConverter;
import com.kdt.localinfo.post.dto.PostDto;
import com.kdt.localinfo.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostConverter postConverter;

    @Transactional
    public Long createPost(PostDto postDto) {
        Post post = postConverter.convertToPost(postDto);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }
}
