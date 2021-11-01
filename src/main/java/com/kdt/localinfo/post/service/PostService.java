package com.kdt.localinfo.post.service;

import com.kdt.localinfo.post.converter.PostConverter;
import com.kdt.localinfo.post.dto.PostDto;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.repository.PostRepository;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public PostDto findDetailPost(Long id) throws NotFoundException {
        return postRepository.findById(id)
                .filter(foundPost -> foundPost.getDeletedAt() == null)
                .map(postConverter::convertToPostDto)
                .orElseThrow(() -> new NotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

    @Transactional
    public List<PostDto> findAllByCategory(Long categoryId) {
        return postRepository.findPostByCategoryId(categoryId)
                .stream().filter(foundPost -> foundPost.getDeletedAt() == null)
                .map(postConverter::convertToPostDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long updatePost(Long id, PostDto postDto) throws NotFoundException {
        postRepository.findById(id)
                .filter(unidentifiedPost -> unidentifiedPost.getDeletedAt() == null)
                .map(foundPost -> {
                    foundPost.updatePost(postDto.getContents(), postDto.getCategory(), postDto.getPhotos());
                    postRepository.save(foundPost);
                    return id;
                })
                .orElseThrow(() -> new NotFoundException("해당 게시글을 찾을 수 없습니다."));
        return id;
    }

    @Transactional
    public Long deletePost(Long id) {
        postRepository.findById(id)
                .filter(unidentifiedPost -> unidentifiedPost.getDeletedAt() == null)
                .map(foundPost -> {
                    foundPost.deletePost();
                    postRepository.save(foundPost);
                    return id;
                })
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 지울 수 없습니다."));
        return 0L;
    }

}
