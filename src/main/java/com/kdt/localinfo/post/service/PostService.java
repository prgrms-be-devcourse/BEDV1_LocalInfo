package com.kdt.localinfo.post.service;

import com.kdt.localinfo.aws.service.AwsS3Service;
import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.category.CategoryRepository;
import com.kdt.localinfo.comment.entity.Comment;
import com.kdt.localinfo.comment.repository.CommentRepository;
import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.photo.PhotoRepository;
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
import java.util.stream.Collectors;

@Service
public class PostService {

    private final String NOT_FOUND_MESSAGE_POST = "해당 게시글을 찾을 수 없습니다.";
    private final String NOT_FOUND_MESSAGE_CATEGORY = "해당 카테고리를 찾을 수 없습니다.";
    private final String NOT_DELETE_MESSAGE = "해당 게시글을 지울 수 없습니다.";
    private final String NOT_FOUND_MESSAGE_USER = "해당 유저를 찾을 수 없습니다.";

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final AwsS3Service awsS3Service;
    private final PhotoRepository photoRepository;

    public PostService(PostRepository postRepository, AwsS3Service awsS3Service, UserRepository userRepository,
                       CategoryRepository categoryRepository, CommentRepository commentRepository, PhotoRepository photoRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
        this.awsS3Service = awsS3Service;
        this.photoRepository = photoRepository;
    }

    @Transactional
    public Post createPost(PostCreateRequest request, List<MultipartFile> multipartFiles) throws IOException, NotFoundException {
        Long userId = Long.valueOf(request.getUserId());
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE_USER));
        List<Photo> postPhotos = fileUpload(multipartFiles);
        List<Photo> savedPhotos = photoRepository.saveAll(postPhotos);
        User user = userRepository.findById(Long.valueOf(request.getUserId()))
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE_USER));

        Category category = categoryRepository.findById(Long.valueOf(request.getCategoryId()))
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE_CATEGORY));

        return Post.builder()
                .contents(request.getContents())
                .category(category)
                .user(user)
                .photos(savedPhotos)
                .build();
    }

    @Transactional
    public PostResponse savePost(PostCreateRequest request, List<MultipartFile> multipartFiles) throws IOException, NotFoundException {
        Post post = createPost(request, multipartFiles);
        Post savedPost = postRepository.save(post);

        return PostResponse.of(post);
    }

    @Transactional
    public PostResponse findDetailPost(Long postId) throws NotFoundException {
        Post post = postRepository.findById(postId).filter(foundPost -> foundPost.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE_POST));

        List<Comment> comments = commentRepository.findCommentsByPostId(postId);
        post.setComments(comments);

        User user = userRepository.findById(post.getUser().getId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE_USER));
        post.setUser(user);

        return PostResponse.of(post);
    }

    @Transactional
    public List<PostResponse> findAllByCategory(Long categoryId) {
        return postRepository.findPostByCategoryId(categoryId)
                .stream().filter(foundPost -> foundPost.getDeletedAt() == null)
                .map(PostResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request, List<MultipartFile> multipartFiles) throws NotFoundException, IOException {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE_CATEGORY));

        List<Photo> postPhotos = fileUpload(multipartFiles);
        List<Photo> savedPhotos = photoRepository.saveAll(postPhotos);

        Post foundPost = postRepository.findById(postId)
                .filter(unidentifiedPost -> unidentifiedPost.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE_POST));

        foundPost.setContents(request.getContents());
        foundPost.setCategory(category);
        foundPost.setPhotos(savedPhotos);

        return PostResponse.of(foundPost);
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

    private List<Photo> fileUpload(List<MultipartFile> multipartFiles) throws IOException {
        List<Photo> uploadPhotos = new ArrayList<>();

        if (multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                Photo upload = Photo.builder()
                        .url(awsS3Service.upload(multipartFile, "post-photo"))
                        .build();
                uploadPhotos.add(upload);
            }
        }
        return uploadPhotos;
    }
}
