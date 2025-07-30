package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.comment.CommentRequest;
import com.sarkhan.backend.dto.comment.CommentResponseForMyComment;
import com.sarkhan.backend.dto.comment.UnCommentedProductResponse;
import jakarta.security.auth.message.AuthException;

import java.util.List;

public interface CommentService {
    String addComment(CommentRequest request);

    List<CommentResponseForMyComment> getCurrentUserComments(Integer page) throws AuthException;

    List<UnCommentedProductResponse> getUnCommentedProducts(Integer page) throws AuthException;

    String updateComment(Long commentId, String content) throws AuthException;

    String deleteComment(Long id) throws AuthException;
}
