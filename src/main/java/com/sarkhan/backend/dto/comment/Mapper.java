package com.sarkhan.backend.dto.comment;

import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.model.product.Product;

import com.sarkhan.backend.repository.user.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;




public class Mapper {

    public static CommentResponse toDto(String nameAndSurname, Comment comment) {
     CommentResponse dto = new CommentResponse.CommentResponseBuilder()
             .content(comment.getContent())
             .nameAndSurname(nameAndSurname)
             .createdAt(comment.getCreatedAt())
             .build();
        return dto;


    }


}
