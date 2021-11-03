package com.kdt.localinfo.post.service;

import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.post.dto.PostCreateRequest;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final S3Service s3Service;

    public PostService(PostRepository postRepository, S3Service s3Service) {
        this.postRepository = postRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    public Long createPost(PostCreateRequest request) throws IOException {
        List<Photo> photoUrls = new ArrayList<>();
        List<MultipartFile> photos = request.getPhotos();

        if (!Objects.isNull(photos)) {
            for (MultipartFile photo : photos) {
                Photo photoEntity = Photo.builder()
                        .url(s3Service.upload(photo))
                        .build();
                photoUrls.add(photoEntity);
            }
        }

        Post post = request.toEntity(photoUrls);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }
}
