package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.comment.CommentRequest;
import com.sarkhan.backend.dto.comment.CommentResponse;
import com.sarkhan.backend.dto.product.ProductResponse;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.CommentService;
import com.sarkhan.backend.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    private final JwtService jwtService;
    @GetMapping("/rated")
    public ResponseEntity<List<ProductResponse>> getRatedProducts(
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);
        String userEmail = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));
        Long userId = user.getId();

        List<ProductResponse> products = commentService.getRatedProducts(userId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/unrated")
    public ResponseEntity<List<ProductResponse>> getUnratedProducts(
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);
        String userEmail = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));
        Long userId = user.getId();

        List<ProductResponse> products = commentService.getUnratedProducts(userId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/ordered")
    public ResponseEntity<List<ProductResponse>> getAllOrderedProducts(
            @RequestHeader("Authorization") String authHeader) {

        String token = extractToken(authHeader);
        String userEmail = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));
        Long userId = user.getId();

        List<ProductResponse> products = commentService.getAllOrderedProducts(userId);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest request,
                                                         @RequestHeader("Authorization") String token) {
        token = token.substring(7); // "Bearer " hissəsini sil
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentResponse response = commentService.createComment(request, user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping
    public ResponseEntity<CommentResponse> updateComment(@RequestParam Long userId,
                                                         @RequestParam Long productId,
                                                         @RequestParam String text,
                                                         @RequestParam int rating) {
        CommentResponse response = commentService.updateComment(userId, productId, text, rating);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@RequestParam Long userId,
                                              @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByProduct(@PathVariable Long productId) {
        List<CommentResponse> responses = commentService.getCommentsByProductId(productId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByUser(@PathVariable Long userId) {
        List<CommentResponse> responses = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(responses);
    }
    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return authHeader.substring(7); // "Bearer " uzunluğundan sonra token başlayır
    }
}