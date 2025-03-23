package com.sarkhan.backend.repository.story;

import com.sarkhan.backend.model.story.Like;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT l FROM Like l WHERE l.fkStoryId=:storyId AND l.fkUserId=:userId")
    Like findLikeByStoryIdAndUserId(@Param("storyId") String storyId, @Param("userId") String userId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
            "FROM Like l WHERE l.fkStoryId = :storyId AND l.fkUserId = :userId")
    boolean existsByStoryIdAndUserId(@Param("storyId") String storyId, @Param("userId") String userId);

    @Query("SELECT COUNT(l.id) FROM Like l WHERE l.fkStoryId=:storyId AND l.likeStatus=:isActive")
    Optional<Long> findLikeCountByStoryId(@Param("storyId")  String storyId, @Param("isActive") String  isActive);

}
