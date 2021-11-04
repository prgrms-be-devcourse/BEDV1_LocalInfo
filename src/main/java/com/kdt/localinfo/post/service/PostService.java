package com.kdt.localinfo.post.service;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.category.CategoryRepository;
import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.post.dto.PostCreateRequest;
import com.kdt.localinfo.post.dto.PostResponse;
import com.kdt.localinfo.post.dto.PostUpdateRequest;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.post.repository.PostRepository;
import com.kdt.localinfo.user.entity.User;
import com.kdt.localinfo.user.repository.UserRepository;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final String NOT_FOUND_MESSAGE = "해당 게시글을 찾을 수 없습니다.";
    private final String NOT_DELETE_MESSAGE = "해당 게시글을 지울 수 없습니다.";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final S3Service s3Service;

    public PostService(PostRepository postRepository, S3Service s3Service,
                       UserRepository userRepository, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    public Post createPost(PostCreateRequest request) throws IOException, NotFoundException {
        List<MultipartFile> multipartFiles = request.getPhotos();

        List<Photo> photos = new ArrayList<>();

        if (!Objects.isNull(multipartFiles)) {
            for (MultipartFile photo : multipartFiles) {
                Photo photoEntity = Photo.builder()
                        .url(s3Service.upload(photo))
                        .build();
                photos.add(photoEntity);
            }
        }

        User user = userRepository.findById(Long.valueOf(request.getUserId()))
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

        Category category = categoryRepository.findById(Long.valueOf(request.getCategoryId()))
                .orElseThrow(() -> new NotFoundException("해당 카테고리 아이디는 존재하지 않습니다."));

        return Post.builder()
                .contents(request.getContents())
                .category(category)
                .user(user)
                .photos(photos)
                .build();
    }

    @Transactional
    public Long savePost(PostCreateRequest request) throws IOException, NotFoundException {
        Post post = createPost(request);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    @Transactional
    public PostResponse findDetailPost(Long postId) throws NotFoundException {
        return PostResponse.of(postRepository.findById(postId)
                .filter(foundPost -> foundPost.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE)));
    }

    @Transactional
    public List<PostResponse> findAllByCategory(Long categoryId) {
        return postRepository.findPostByCategoryId(categoryId)
                .stream().filter(foundPost -> foundPost.getDeletedAt() == null)
                .map(PostResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long updatePost(PostUpdateRequest request) throws NotFoundException, IOException {
        List<MultipartFile> multipartFiles = request.getPhotos();
        List<Photo> photos = new ArrayList<>();

        if (!Objects.isNull(multipartFiles)) {
            for (MultipartFile photo : multipartFiles) {
                Photo photoEntity = Photo.builder()
                        .url(s3Service.upload(photo))
                        .build();
                photos.add(photoEntity);
            }
        }

        Category category = categoryRepository.findById(Long.valueOf(request.getCategoryId()))
                .orElseThrow(() -> new NotFoundException("해당 카테고리 아이디는 존재하지 않습니다."));

        Post foundPost = postRepository.findById(Long.valueOf(request.getPostId()))
                .filter(unidentifiedPost -> unidentifiedPost.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        foundPost.setContents(request.getContents());
        foundPost.setCategory(category);
        foundPost.setPhotos(photos);

        Post saved = postRepository.save(foundPost);
        return saved.getId();
    }

    @Transactional
    public Long deletePost(Long postId) {
        postRepository.findById(postId)
                .filter(unidentifiedPost -> unidentifiedPost.getDeletedAt() == null)
                .map(foundPost -> {
                    foundPost.deletePost();
                    postRepository.save(foundPost);
                    return postId;
                })
                .orElseThrow(() -> new IllegalArgumentException(NOT_DELETE_MESSAGE));
        return postId;
    }
}
