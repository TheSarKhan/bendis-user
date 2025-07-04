package com.sarkhan.backend.repository.comment;


import com.sarkhan.backend.dto.comment.CommentResponse;
import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.model.user.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {


    // İstifadəçinin rəy verdiyi məhsul ID-ləri
    @Query("SELECT DISTINCT c.productId FROM Comment c WHERE c.userId = :userId")
    List<Long> findProductIdsByUserId(@Param("userId") Long userId);
    List<Comment> findCommentByUserId(Long userId);

    List<Comment>findCommentByProductIdOrderByCreatedAtDesc(Long productId);

    List<Comment>findCommentByProductId(Long productId);

    Optional<Comment> findByUserIdAndProductId(Long userId, Long productId);


}
