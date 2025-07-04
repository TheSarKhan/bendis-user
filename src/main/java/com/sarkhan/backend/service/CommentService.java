package com.sarkhan.backend.service;

import com.sarkhan.backend.dto.comment.CommentRequest;
import com.sarkhan.backend.dto.comment.CommentResponse;
import com.sarkhan.backend.dto.product.ProductResponse;
import com.sarkhan.backend.model.comment.Comment;

import java.util.List;

public interface CommentService {

    //Comment addComment(CommentRequest request);

    void deleteComment(Long userId,Long commentId);
    public List<ProductResponse> getAllOrderedProducts(Long userId);
    List<ProductResponse> getRatedProducts(Long userId);
    List<ProductResponse> getUnratedProducts(Long userId);
    CommentResponse updateComment(Long userId, Long productId,String text,int rating);

    List<CommentResponse> getCommentsByProductId(Long productId); // Long -> String

    List<CommentResponse> getCommentsByUserId(Long userId);

    CommentResponse createComment(CommentRequest commentRequest,Long userId);
}
