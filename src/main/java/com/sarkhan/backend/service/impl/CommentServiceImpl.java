package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.comment.CommentRequest;
import com.sarkhan.backend.dto.comment.CommentResponseForMyComment;
import com.sarkhan.backend.dto.comment.UnCommentedProductResponse;
import com.sarkhan.backend.mapper.comment.CommentMapper;
import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.model.enums.OrderStatus;
import com.sarkhan.backend.model.order.Order;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.cart.CartRepository;
import com.sarkhan.backend.repository.comment.CommentRepository;
import com.sarkhan.backend.repository.order.OrderRepository;
import com.sarkhan.backend.repository.product.items.UserFavoriteProductRepository;
import com.sarkhan.backend.service.CommentService;
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.impl.product.util.UserUtil;
import com.sarkhan.backend.service.product.ProductService;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final ProductService productService;

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final UserFavoriteProductRepository userFavoriteProductRepository;

    private final OrderRepository orderRepository;

    private final CartRepository cartRepository;

    @Override
    public String addComment(CommentRequest request) {
        log.info("Someone is trying to add comment");
        try {
            User currentUser = UserUtil.getCurrentUser(userService, log);
            if (!productService.getMyDeliveredProductId().contains(request.productId())) {
                log.info("{} try to add comment in not delivered product.", currentUser.getFullName());
                return "You cannot add comment in not delivered product.";
            }
            if (commentRepository.getByUserIdAndProductId(currentUser.getId(), request.productId()).isPresent()) {
                log.info("{} try to add 2 or more comments in one product.", currentUser.getFullName());
                return "You cannot add 2 or more comments in one product.";
            }
            Comment comment = CommentMapper.mapRequestToComment(request);
            productService.giveRating(comment.getProductId(), comment.getRating());
            comment.setUserId(currentUser.getId());
            comment.setUserName(currentUser.getFullName());
            commentRepository.save(comment);
            log.info("Comment added successfully");
            return "Successfully added comment";
        } catch (AuthException e) {
            log.error(e.getMessage());
            return "Please login first.";
        }
    }

    @Override
    public List<CommentResponseForMyComment> getCurrentUserComments(Integer page) throws AuthException {
        log.info("Someone is trying to get current user comments");
        User currentUser = UserUtil.getCurrentUser(userService, log);
        Pageable pageable = PageRequest.of(page - 1, 10);
        List<Comment> comments = commentRepository.getByUserId(currentUser.getId(), pageable);
        log.info("Current user comments count: {}", comments.size());
        List<CommentResponseForMyComment> list = comments.stream().map(comment ->
                        CommentMapper.mapCommentToCommentResponse(comment,
                                productService.getById(comment.getProductId()),
                                userFavoriteProductRepository
                                        .getByProductIdAndUserId(
                                                comment.getProductId(),
                                                comment.getUserId())
                                        .isPresent()))
                .toList();
        log.info("Current user comments : {}", list);
        return list;
    }

    @Override
    public List<UnCommentedProductResponse> getUnCommentedProducts(Integer page) throws AuthException {
        log.info("Someone is trying to get current user un commented products.");
        User currentUser = UserUtil.getCurrentUser(userService, log);
        List<Long> commentedProductIds = getCurrentUserAllComments(currentUser)
                .stream()
                .map(Comment::getProductId)
                .toList();
        return orderRepository
                .findByUserId(currentUser.getId())
                .stream()
                .filter(order -> OrderStatus.DELIVERED.equals(order.getOrderStatus()))
                .sorted(Comparator.comparing(Order::getUpdatedAt))
                .map(order -> cartRepository
                        .findById(order.getCartId())
                        .orElseThrow()
                        .getCartItems()
                        .stream()
                        .map(cartItem -> productService.getById(cartItem.getProductId()))
                        .filter(product -> !commentedProductIds.contains(product.getId()))
                        .collect(Collectors.toSet()))
                .reduce((a, b) -> {
                    a.addAll(b);
                    return a;
                })
                .orElse(new HashSet<>())
                .stream()
                .limit(page * 10)
                .map(product -> CommentMapper.mapProductToUnCommentedProductResponse(
                        product,
                        userFavoriteProductRepository
                                .getByProductIdAndUserId(
                                        product.getId(),
                                        currentUser.getId())
                                .isPresent()))
                .toList();
    }

    @Override
    public String updateComment(Long commentId, String content) throws AuthException {
        log.info("Someone is trying to update comment.");
        User currentUser = UserUtil.getCurrentUser(userService, log);
        Comment comment = getCommentById(commentId);
        if (currentUser.getId().equals(comment.getUserId())) {
            log.info("Someone is trying to update other user's comment.");
            return "You cannot change other user's comment.";
        }
        comment.setContent(content);
        commentRepository.save(comment);
        log.info("Successfully updated comment.");
        return "Update is successful.";
    }

    @Override
    public String deleteComment(Long id) throws AuthException {
        log.info("Someone is trying to delete comment");
        User currentUser = UserUtil.getCurrentUser(userService, log);
        Comment comment = getCommentById(id);
        if (!currentUser.getId().equals(comment.getUserId())) {
            log.info("Someone is trying to delete other user's comment.");
            return "You cannot change other user's comment.";
        }
        commentRepository.deleteById(id);
        productService.removeRating(comment.getProductId(), currentUser.getId());
        log.info("Comment deleted successfully");
        return "Delete is successful.";
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("Comment not found");
            return new NoSuchElementException("Comment not found");
        });
    }

    private List<Comment> getCurrentUserAllComments(User currentUser) {
        return commentRepository.getByUserId(currentUser.getId());
    }
}
