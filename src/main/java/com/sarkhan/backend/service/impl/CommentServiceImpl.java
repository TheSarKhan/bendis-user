package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.comment.CommentRequest;
import com.sarkhan.backend.dto.comment.CommentResponse;
import com.sarkhan.backend.dto.comment.Mapper;
import com.sarkhan.backend.dto.product.ProductMapper;
import com.sarkhan.backend.dto.product.ProductResponse;
import com.sarkhan.backend.handler.exception.NotFoundException;
import com.sarkhan.backend.handler.exception.UnauthorizedException;
import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.comment.CommentRepository;
import com.sarkhan.backend.repository.order.OrderItemRepository;
import com.sarkhan.backend.repository.product.ProductRepository;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sarkhan.backend.dto.comment.Mapper.toDto;
import static com.sarkhan.backend.dto.comment.Mapper.toDtoList;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    // Bütün istifadəçinin sifariş etdiyi məhsullar (və varsa comment-ləri)
    public List<ProductResponse> getAllOrderedProducts(Long userId) {
        List<Long> orderedProductIds = orderItemRepository.findOrderedProductIdsByUserId(userId);
        List<Product> products = productRepository.findAllById(orderedProductIds);

        List<Comment> userComments = commentRepository.findCommentByUserId(userId);
        Map<Long, Comment> productIdToComment = userComments.stream()
                .collect(Collectors.toMap(Comment::getProductId, c -> c));

        return products.stream()
                .map(product -> {
                    Comment comment = productIdToComment.get(product.getId());
                    return ProductMapper.toResponse(product, comment);
                })
                .toList();
    }

    // İstifadəçinin rəy yazdığı məhsullar
    public List<ProductResponse> getRatedProducts(Long userId) {
        List<Comment> userComments = commentRepository.findCommentByUserId(userId);
        List<Product> products = productRepository.findAllById(
                userComments.stream()
                        .map(Comment::getProductId)
                        .toList()
        );
        Map<Long, Comment> productIdToCommentMap = userComments.stream()
                .collect(Collectors.toMap(Comment::getProductId, c -> c));

        return products.stream()
                .map(product -> {
                    Comment comment = productIdToCommentMap.get(product.getId());
                    return ProductMapper.toResponse(product, comment);
                })
                .toList();
    }

    // İstifadəçinin rəy yazmadığı məhsullar (sifariş etdiyi, amma rəyləndirmədiyi)
    public List<ProductResponse> getUnratedProducts(Long userId) {
        List<Long> orderedProductIds = orderItemRepository.findOrderedProductIdsByUserId(userId);
        List<Long> ratedProductIds = commentRepository.findProductIdsByUserId(userId);

        List<Long> unratedProductIds = orderedProductIds.stream()
                .filter(id -> !ratedProductIds.contains(id))
                .toList();

        List<Product> products = productRepository.findAllById(unratedProductIds);

        return products.stream()
                .map(product -> ProductMapper.toResponse(product, null))
                .toList();
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (!Objects.equals(comment.getUserId(), userId)) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }
        commentRepository.delete(comment);
    }


    @Override
    @Transactional
    public CommentResponse updateComment(Long userId, Long productId, String text, int rating) {
        Comment comment = commentRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (!Objects.equals(comment.getUserId(), userId)) {
            throw new UnauthorizedException("You are not authorized to update this comment");
        }

        comment.setText(text);
        comment.setRating(rating);
        Comment updated = commentRepository.save(comment);
        return toDto(updated);
    }


    @Override
    public List<CommentResponse> getCommentsByProductId(Long productId) {
        List<Comment> comments = commentRepository.findCommentByProductId(productId);
        return toDtoList(comments);
    }

    @Override
    public List<CommentResponse> getCommentsByUserId(Long userId) {
        List<Comment> commentsByUserId = commentRepository.findCommentByUserId(userId);
        return toDtoList(commentsByUserId);
    }

    @Transactional
    @Override
    public CommentResponse createComment(CommentRequest commentRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Product product = productRepository.findById(commentRequest.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        boolean hasOrdered = orderItemRepository.existsByUserIdAndProductId(userId, commentRequest.getProductId());
        if (!hasOrdered) {
            throw new UnauthorizedException("You can only comment on products you have ordered");
        }

        Comment comment = new Comment();
        comment.setText(commentRequest.getText());
        comment.setRating(commentRequest.getRating());
        comment.setUserId(userId);
        comment.setProductId(commentRequest.getProductId());
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return toDto(savedComment);
    }
}
