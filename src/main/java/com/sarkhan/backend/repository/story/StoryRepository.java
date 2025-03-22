package com.sarkhan.backend.repository.story;

import com.sarkhan.backend.model.story.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

}
