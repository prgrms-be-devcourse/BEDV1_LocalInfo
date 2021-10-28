package com.kdt.localinfo.comment.dto;

public enum CommentDepth {
    ZERO(0L), ONE(1L);

    Long depth;

    CommentDepth(Long num) {
        this.depth = num;
    }

    public Long getDepth() {
        return depth;
    }
}
