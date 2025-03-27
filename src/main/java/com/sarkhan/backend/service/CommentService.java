package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.comment.CommentRequest;
import com.sarkhan.backend.dto.comment.CommentResponse;
import com.sarkhan.backend.model.comment.Comment;

import java.util.List;

public interface CommentService {

    Comment addComment(CommentRequest request);

    void deleteComment(Long commentId); // commentId Long qalır, çünki entity-də id Long-dur

    Comment updateComment(Long commentId, CommentRequest request);

    List<CommentResponse> getCommentsByProductId(String productId); // Long -> String

    List<CommentResponse> getCommentsByUserId(String userId);
}
