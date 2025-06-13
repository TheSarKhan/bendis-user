package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.story.StoryResponseDTO;
import com.sarkhan.backend.model.enums.LikeType;
import com.sarkhan.backend.model.enums.Role;
import com.sarkhan.backend.model.story.Story;
import com.sarkhan.backend.model.story.item.Like;
import com.sarkhan.backend.model.user.Seller;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.seller.SellerRepository;
import com.sarkhan.backend.repository.story.LikeRepository;
import com.sarkhan.backend.repository.story.StoryRepository;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.story.LikeService;
import com.sarkhan.backend.service.story.StoryService;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService, LikeService {

    private final CloudinaryService cloudinaryService;

    private final UserService userService;

    private final SellerRepository sellerRepository;

    private final StoryRepository storyRepository;

    private final LikeRepository likeRepository;

    @Override
    public StoryResponseDTO create(Long sellerId,
                                   String description,
                                   MultipartFile mainContent,
                                   MultipartFile logo) throws IOException {
        log.info("Some seller try to create story.");

        String mainContentUrl = cloudinaryService.uploadFile(mainContent, "story").getUrl();
        String logoUrl = cloudinaryService.uploadFile(logo, "story").getUrl();

        return new StoryResponseDTO(
                storyRepository.save(Story.builder().
                        sellerId(sellerId).
                        mainContentUrl(mainContentUrl).
                        logoUrl(logoUrl).
                        description(description).
                        build()),
                getSellerById(sellerId));
    }

    @Override
    public List<Story> getAll() {
        log.info("Someone try to get all stories.");
        return storyRepository.findAll();
    }

    @Override
    public List<Story> getForHomePage() {
        log.info("GetForHomePage story methode start working.");
        return storyRepository.getForHomePage(LocalDateTime.now().minusDays(1));
    }

    @Override
    public List<Story> getBySellerId(Long sellerId) {
        log.info("Someone try to get stories by seller id.");
        return storyRepository.getBySellerId(sellerId);
    }

    @Override
    public StoryResponseDTO getByIdAndAddView(Long id) {
        log.info("Someone try to get stories by id.");
        Story story = getById(id);
        try {
            Long userId = getCurrentUser().getId();
            List<Long> view = story.getView();
            if (!view.contains(userId)) {
                view.add(userId);
                story.setView(view);
                story = storyRepository.save(story);
            }
        } catch (AuthException ignored) {
        }
        return new StoryResponseDTO(story,
                getSellerById(story.getSellerId()));
    }

    @Override
    public StoryResponseDTO toggleLikeOrDislike(Long storyId, String likeText) throws AuthException {
        Long userId = getCurrentUser().getId();
        Optional<Like> likeOptional = likeRepository.getByStoryIdAndUserId(storyId, userId);
        Story story = getById(storyId);
        LikeType likeType = LikeType.valueOf(likeText.toUpperCase());

        if (likeOptional.isPresent()) {
            Like like = likeOptional.get();

            if (likeType.equals(like.getLikeType())) {
                if (LikeType.LIKE.equals(likeType)) story.setLikeCount(story.getLikeCount() - 1);
                else story.setDislikeCount(story.getDislikeCount() - 1);
            } else {
                if (LikeType.LIKE.equals(likeType)) {
                    story.setLikeCount(story.getLikeCount() + 1);
                    story.setDislikeCount(story.getDislikeCount() - 1);
                } else {
                    story.setLikeCount(story.getLikeCount() - 1);
                    story.setDislikeCount(story.getDislikeCount() + 1);
                }

                likeRepository.save(Like.builder().
                        likeType(LikeType.LIKE.equals(likeType) ? LikeType.DISLIKE : LikeType.LIKE).
                        storyId(like.getStoryId()).
                        userId(like.getUserId()).
                        build());
            }
            likeRepository.delete(like);
        } else {
            if (LikeType.LIKE.equals(likeType)) story.setLikeCount(story.getLikeCount() + 1);
            else story.setDislikeCount(story.getDislikeCount() + 1);
            likeRepository.save(Like.builder().
                    likeType(likeType).
                    storyId(storyId).
                    userId(userId).
                    build());
        }

        return new StoryResponseDTO(storyRepository.save(story),
                getSellerById(story.getSellerId()));
    }

    @Override
    public StoryResponseDTO update(Long sellerId,
                                   Long id,
                                   String description,
                                   MultipartFile mainContent,
                                   MultipartFile logo) throws IOException, AuthException {
        log.info("Someone try to update story.");

        Story story = getById(id);

        if ((sellerId == null || !sellerId.equals(story.getSellerId()) &&
                                 !Role.ADMIN.equals(getCurrentUser().getRole()))) {
            log.warn("This user doesn't have access update story methode.");
            return new StoryResponseDTO(story, getSellerById(story.getSellerId()));
        }

        cloudinaryService.deleteFile(story.getMainContentUrl());
        cloudinaryService.deleteFile(story.getLogoUrl());

        String mainContentUrl = cloudinaryService.uploadFile(mainContent, "story").getUrl();
        String logoUrl = cloudinaryService.uploadFile(logo, "story").getUrl();

        story.setMainContentUrl(mainContentUrl);
        story.setLogoUrl(logoUrl);
        story.setDescription(description);

        return new StoryResponseDTO(storyRepository.save(story),
                getSellerById(story.getSellerId()));
    }

    @Override
    public void delete(Long sellerId, Long id) throws AuthException {
        log.info("Someone try to delete story.");

        if ((sellerId == null || !sellerId.equals(getById(id).getSellerId()) &&
                                 !Role.ADMIN.equals(getCurrentUser().getRole()))) {
            log.warn("This user doesn't have access delete story methode.");
            return;
        }
        storyRepository.deleteById(id);
    }

    public Story getById(Long id) {
        return storyRepository.findById(id).orElseThrow(() -> {
            log.info("Cannot find story by " + id + " id.");
            return new NoSuchElementException("Cannot find story by " + id + " id.");
        });
    }

    private User getCurrentUser() throws AuthException {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (email == null) {
            log.warn("User doesn't login!!!");
            throw new AuthException("User doesn't login!!!");
        }
        return userService.getByEmail(email);
    }

    private Seller getSellerById(Long sellerId) {
        return sellerRepository.findById(sellerId).orElseThrow(() -> {
            log.info("Cannot find seller.");
            return new NoSuchElementException("Cannot find seller.");
        });
    }
}
