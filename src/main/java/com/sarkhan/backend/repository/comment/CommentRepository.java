package com.sarkhan.backend.repository.comment;


import com.sarkhan.backend.model.comment.Comment;
import com.sarkhan.backend.model.user.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Bütün şərhləri productId-yə görə tap
    @Query("SELECT c FROM Comment c WHERE c.productId = :productId")
    List<Comment> findByProductId(String productId);

    // Bütün şərhləri userId-yə görə tap
    @Query("SELECT c FROM Comment c WHERE c.userId= :userId")
    List<Comment> findByUserId(@Param("userId") String userId);


    //bunu userrepostiroy atmisam
//    @Query("SELECT  u.nameAndSurname  FROM User u WHERE u.id= :userId")
//    String findUserNameByUserId(String userId);
//
//    @Query("SELECT  p.  FROM Product p WHERE p.id= :productId")
////    String findUserNameByUserId(String userId);

    // Müəyyən bir istifadəçinin müəyyən bir məhsula yazdığı şərhləri tap
    @Query("SELECT c FROM Comment c WHERE c.userId = :userId AND c.productId = :productId")
    List<Comment> findByUserIdAndProductId(String userId, String productId);
}
