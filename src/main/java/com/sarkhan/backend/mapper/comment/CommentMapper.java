package com.sarkhan.backend.mapper.comment;

import com.sarkhan.backend.dto.comment.CommentRequest;
import com.sarkhan.backend.dto.comment.CommentResponse;
import com.sarkhan.backend.dto.comment.CommentResponseForMyComment;
import com.sarkhan.backend.dto.comment.UnCommentedProductResponse;
import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.UserFavoriteProduct;

import java.util.List;
import java.util.Optional;

public class CommentMapper {
    public static Comment mapRequestToComment(CommentRequest request) {
        return Comment.builder()
                .productId(request.productId())
                .rating(request.rating())
                .content(request.content())
                .build();
    }

    public static CommentResponseForMyComment mapCommentToCommentResponse(Comment comment, Product product, boolean isFavorite) {
        return new CommentResponseForMyComment(
                comment.getProductId(),
                product.getColorAndSizes().getFirst().getImageUrls().getFirst(),
                product.getName(),
                product.getDescription(),
                product.getOriginalPrice(),
                product.getDiscountedPrice(),
                isFavorite,
                product.getRating(),
                product.getRatings().size(),
                comment.getId(),
                comment.getContent()
        );
    }

    public static CommentResponse mapCommentToCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getUserName(),
                comment.getContent(),
                comment.getUsefulCount(),
                comment.getRating(),
                comment.getUpdatedAt()==null?comment.getCreatedAt():comment.getUpdatedAt()
        );
    }

    public static List<CommentResponse> mapCommentsToCommentResponses(List<Comment> comments) {
        return comments.stream().map(CommentMapper::mapCommentToCommentResponse).toList();
    }

    public static UnCommentedProductResponse mapProductToUnCommentedProductResponse(Product product, boolean isFavorite) {
        return new UnCommentedProductResponse(
                product.getId(),
                product.getColorAndSizes().getFirst().getImageUrls().getFirst(),
                product.getName(),
                product.getDescription(),
                product.getOriginalPrice(),
                product.getDiscountedPrice(),
                isFavorite,
                product.getRating(),
                product.getRatings().size());
    }
}
