package com.sarkhan.backend.controller;

import com.sarkhan.backend.dto.comment.CommentRequest;
import com.sarkhan.backend.dto.comment.CommentResponseForMyComment;
import com.sarkhan.backend.dto.comment.UnCommentedProductResponse;
import com.sarkhan.backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Comment Controller", description = "Operations related to comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(
            summary = "Add a new comment",
            description = "Adds a new comment to a post or another comment."
    )
    public ResponseEntity<String> addComment(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(request));
    }

    @GetMapping
    @Operation(
            summary = "Get user's own comments",
            description = "Retrieves comments made by the currently authenticated user. Supports pagination."
    )
    public ResponseEntity<List<CommentResponseForMyComment>> getMyComments(@RequestParam Integer page) throws AuthException {
        return ResponseEntity.ok(commentService.getCurrentUserComments(page));
    }

    @GetMapping("/uncommented-product")
    @Operation(
            summary = "Get user's uncommented products",
            description = "Returns a list of products that the authenticated user has not commented on yet."
    )
    public ResponseEntity<List<UnCommentedProductResponse>> getMyUnCommentedProducts(@RequestParam Integer page) throws AuthException {
        return ResponseEntity.ok(commentService.getUnCommentedProducts(page));
    }

    @PutMapping
    @Operation(
            summary = "Update a comment",
            description = "Updates the content of a specific comment belonging to the authenticated user."
    )
    public ResponseEntity<String> updateComment(@RequestBody Long commentId,
                                                @RequestBody String content) throws AuthException {
        return ResponseEntity.ok(commentService.updateComment(commentId, content));
    }

    @DeleteMapping
    @Operation(
            summary = "Delete a comment",
            description = "Deletes a specific comment made by the authenticated user."
    )
    public ResponseEntity<String> deleteComment(@RequestParam Long commentId) throws AuthException {
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}