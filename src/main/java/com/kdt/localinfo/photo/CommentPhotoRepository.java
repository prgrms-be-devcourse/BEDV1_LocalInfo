package com.kdt.localinfo.photo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentPhotoRepository extends JpaRepository<CommentPhoto, Long> {
    List<CommentPhoto> findAllByCommentId(Long commentId);
}
