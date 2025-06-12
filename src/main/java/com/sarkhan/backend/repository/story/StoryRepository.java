package com.sarkhan.backend.repository.story;

import com.sarkhan.backend.model.story.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    @Query("from Story where createdAt >= :yesterday order by viewCount, likeCount")
    List<Story> getForHomePage(LocalDateTime yesterday);

    List<Story> getBySellerId(Long sellerId);
}
