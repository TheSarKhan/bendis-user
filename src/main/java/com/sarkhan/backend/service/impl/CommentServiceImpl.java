package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.comment.CommentRequest;
import com.sarkhan.backend.dto.comment.CommentResponse;
import com.sarkhan.backend.dto.comment.Mapper;
import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.repository.comment.CommentRepository;

import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.sarkhan.backend.dto.comment.Mapper.toDto;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    @Override
    public Comment addComment(CommentRequest request) {
        Comment comment = Comment.builder()
                .userId(request.getUserId())    // Sıra düzəldildi
                .productId(request.getProductId()) // Sıra düzəldildi
                .content(request.getContent())
                .build(); // createdAt avtomatik doldurulacaq
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);

    }

    @Override
    public Comment updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(request.getContent());
        // userId və productId dəyişmir, yalnız content yenilənir
        return commentRepository.save(comment);
    }

    @Override
    public List<CommentResponse> getCommentsByProductId(String productId) {
        List<Comment> comments = commentRepository.findByProductId(productId);
        return comments.stream()
                .map(comment -> {
                    String nameAndSurname = userRepository.findUserNameByUserId(comment.getUserId());
                    return toDto(nameAndSurname, comment);
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<CommentResponse> getCommentsByUserId(String userId) {
       String nameAndSurname = userRepository.findUserNameByUserId(userId);
       List<Comment>comments=commentRepository.findByUserId(userId);
        return comments.stream().map(c-> toDto(nameAndSurname,c)).toList();

    }
}
