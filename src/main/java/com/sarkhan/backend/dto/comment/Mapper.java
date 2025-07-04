package com.sarkhan.backend.dto.comment;

import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.model.product.Product;

import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.CommentService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;




public class Mapper {

    public static CommentResponse toDto(Comment comment) {
     CommentResponse dto = new CommentResponse.CommentResponseBuilder()
             .id(comment.getId())
             .text(comment.getText())
             .rating(comment.getRating())
             .createdAt(comment.getCreatedAt())
             .productId(comment.getProductId())
             .userId(comment.getUserId())
             .build();
        return dto;

    }
    public static List<CommentResponse> toDtoList(List<Comment> comments) {
        return comments.stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
    }



}
