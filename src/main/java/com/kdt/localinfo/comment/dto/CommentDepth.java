package com.kdt.localinfo.comment.dto;

public enum CommentDepth {
    ONE(0L), ZERO(1L);

    Long depth;

    CommentDepth(Long num) {
        this.depth = num;
    }

    public Long getDepth() {
        return depth;
    }
}
