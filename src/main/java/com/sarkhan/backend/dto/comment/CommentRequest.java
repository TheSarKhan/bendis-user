package com.sarkhan.backend.dto.comment;

public record CommentRequest(
        double rating,
        Long productId,
        String content){

}