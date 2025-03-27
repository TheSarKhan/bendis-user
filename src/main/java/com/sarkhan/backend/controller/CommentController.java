package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.comment.CommentRequest;
import com.sarkhan.backend.dto.comment.CommentResponse;
import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Yeni şərh əlavə et
    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody CommentRequest request) {
        Comment createdComment = commentService.addComment(request);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    // Şərhi sil
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }

    // Şərhi yenilə
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId,
                                                 @RequestBody CommentRequest request) {
        Comment updatedComment = commentService.updateComment(commentId, request);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    // Product-a aid şərhləri göstər
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByProductId(@PathVariable String productId) {
        List<CommentResponse> comments = commentService.getCommentsByProductId(productId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // User-ə aid şərhləri göstər
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByUserId(@PathVariable String userId) {
        List<CommentResponse> comments = commentService.getCommentsByUserId(userId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}