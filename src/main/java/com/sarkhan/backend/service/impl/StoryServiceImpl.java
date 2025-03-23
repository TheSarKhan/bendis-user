package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.model.story.Like;
import com.sarkhan.backend.model.story.Story;
import com.sarkhan.backend.repository.story.LikeRepository;
import com.sarkhan.backend.repository.story.StoryRepository;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.LikeService;
import com.sarkhan.backend.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService, LikeService {

    private final CloudinaryService cloudinaryService;

    private final StoryRepository storyRepository;

    private final LikeRepository likeRepository;

    @Override
    public String addAndRemoveLike(String storyId, String userId) {

        if (!likeRepository.existsByStoryIdAndUserId(storyId, userId)) {
            Like like = Like.builder()
                    .fkStoryId(storyId)
                    .fkUserId(userId)
                    .likeStatus("ACTIVE")
                    .build();

            likeRepository.save(like);

        } else if (likeRepository.findLikeByStoryIdAndUserId(storyId, userId)
                        .getLikeStatus().equals("ACTIVE")) {

            Like like = likeRepository.findLikeByStoryIdAndUserId(storyId, userId);

            like.setLikeStatus("INACTIVE");

            likeRepository.save(like);

        } else {
            Like like = likeRepository.findLikeByStoryIdAndUserId(storyId, userId);

            like.setLikeStatus("ACTIVE");
            likeRepository.save(like);
        }

        return "Success";
    }

    @Override
    public Optional<Long> getLike(String storyId) {
        return likeRepository.findLikeCountByStoryId(storyId,"ACTIVE").orElseThrow(()->
                new RuntimeException("Like not found")).describeConstable();
    }


    @Override
    public Story createStory(MultipartFile storyImage, MultipartFile adImage) throws IOException {

        String storyImageUrl = cloudinaryService.uploadFile(storyImage, "story_images");

        String adImageUrl = cloudinaryService.uploadFile(adImage, "product_ads");


        Story story = new Story();
        story.setStoryImageUrl(storyImageUrl);
        story.setAdImageUrl(adImageUrl);
        story.setCreatedAt(LocalDateTime.now().toString());


        return storyRepository.save(story);
    }

    @Override
    public List<Story> getAllStories() {
        return storyRepository.findAll();
    }

    @Override
    public String getAdImageUrl(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story is not found: " + storyId));
        return story.getAdImageUrl();
    }
}
