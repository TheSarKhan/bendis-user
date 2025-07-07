package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.cloudinary.CloudinaryUploadResponse;
import com.sarkhan.backend.dto.story.StoryResponseDTO;
import com.sarkhan.backend.model.enums.LikeType;
import com.sarkhan.backend.model.story.Story;
import com.sarkhan.backend.model.story.item.Like;
import com.sarkhan.backend.model.user.Seller;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.SellerRepository;
import com.sarkhan.backend.repository.story.LikeRepository;
import com.sarkhan.backend.repository.story.StoryRepository;
import com.sarkhan.backend.service.CloudinaryService;
import com.sarkhan.backend.service.UserService;
import jakarta.security.auth.message.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoryServiceImplTest {
    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private UserService userService;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private StoryServiceImpl storyService;

    @Mock
    MultipartFile mainContent;

    @Mock
    MultipartFile logo;

    private Story story;

    private Seller seller;

    private User user;

    private Like like;

    private Like dislike;

    @BeforeEach
    void setUp() {
        story = Story.builder().
                id(1L).
                sellerId(1L).
                mainContentUrl("Some video url").
                logoUrl("Some photo url").
                description("This is simple description.").
                likeCount(0L).
                dislikeCount(0L).
                view(new ArrayList<>()).
                build();
        seller = Seller.builder().
                id(1L).
                build();
        user = User.builder().
                id(1L).
                email("test@example.com").
                build();
        like = Like.builder().
                likeType(LikeType.LIKE).
                storyId(1L).
                userId(1L).
                build();
        dislike = Like.builder().
                likeType(LikeType.DISLIKE).
                storyId(1L).
                userId(1L).
                build();
    }

    @BeforeEach
    void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);
        lenient().when(authentication.getPrincipal()).thenReturn("test@example.com");
        lenient().when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void testAdd() throws IOException {
        var mainContentResponse = new CloudinaryUploadResponse("Some video url", "publicId");
        var logoResponse = new CloudinaryUploadResponse("Some photo url", "publicId");

        when(cloudinaryService.uploadFile(mainContent, "story")).thenReturn(mainContentResponse);
        when(cloudinaryService.uploadFile(logo, "story")).thenReturn(logoResponse);

        when(storyRepository.save(any(Story.class))).thenAnswer(inv -> inv.getArgument(0));

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        StoryResponseDTO response = storyService.add(1L,
                "This is simple description.",
                mainContent,
                logo);

        assertEquals("This is simple description.", response.story().getDescription());
        assertEquals("Some video url", response.story().getMainContentUrl());
        assertEquals("Some photo url", response.story().getLogoUrl());
        assertEquals(1L, response.seller().getId());

        verify(cloudinaryService).uploadFile(mainContent, "story");
        verify(cloudinaryService).uploadFile(logo, "story");
        verify(storyRepository).save(any(Story.class));
    }

    @Test
    void testGetAll() {
        when(storyRepository.findAll()).thenReturn(List.of(story));

        List<Story> all = storyService.getAll();

        assertEquals(1L, all.getFirst().getSellerId());
        assertEquals("This is simple description.", all.getFirst().getDescription());

        verify(storyRepository).findAll();
    }

    @Test
    void testGetForHomePage() {
        when(storyRepository.getForHomePage(any(LocalDateTime.class))).thenReturn(List.of(story));

        List<Story> all = storyService.getForHomePage();

        assertEquals(1L, all.getFirst().getSellerId());
        assertEquals("This is simple description.", all.getFirst().getDescription());

        verify(storyRepository).getForHomePage(any(LocalDateTime.class));
    }

    @Test
    void testGetBySellerId() {
        when(storyRepository.getBySellerId(anyLong()))
                .thenReturn(List.of(story));

        List<Story> all = storyService.getBySellerId(1L);

        assertEquals(1L, all.getFirst().getSellerId());
        assertEquals("This is simple description.", all.getFirst().getDescription());

        verify(storyRepository).getBySellerId(anyLong());
    }

    @Test
    void testGetByIdAndAddView() {
        when(storyRepository.findById(anyLong())).thenReturn(Optional.of(story));
        when(userService.getByEmail("test@example.com")).thenReturn(user);
        when(sellerRepository.findById(anyLong())).thenReturn(Optional.of(seller));
        when(storyRepository.save(any(Story.class))).thenReturn(story);

        StoryResponseDTO response = storyService.getByIdAndAddView(1L);

        assertEquals(1L, response.story().getSellerId());
        assertEquals("This is simple description.", response.story().getDescription());
        assertTrue(response.story().getView().contains(1L));

        verify(storyRepository).findById(anyLong());
        verify(userService).getByEmail(anyString());
    }

    @Test
    void testToggleLikeOrDislikeFirstClickLike() throws AuthException {
        when(userService.getByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.getByStoryIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(storyRepository.findById(anyLong())).thenReturn(Optional.of(story));
        when(sellerRepository.findById(anyLong())).thenReturn(Optional.of(seller));
        when(storyRepository.save(any(Story.class))).thenReturn(story);

        StoryResponseDTO response = storyService.toggleLikeOrDislike(1L, "like");

        assertEquals(1L, response.story().getLikeCount());
        assertEquals(0L, response.story().getDislikeCount());

        verify(userService).getByEmail(anyString());
        verify(likeRepository).getByStoryIdAndUserId(anyLong(), anyLong());
        verify(likeRepository).save(any(Like.class));
        verify(storyRepository).findById(anyLong());
        verify(storyRepository).save(any(Story.class));
    }

    @Test
    void testToggleLikeOrDislikeFirstClickDislike() throws AuthException {
        when(userService.getByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.getByStoryIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(storyRepository.findById(anyLong())).thenReturn(Optional.of(story));
        when(likeRepository.save(any(Like.class))).thenReturn(dislike);
        when(sellerRepository.findById(anyLong())).thenReturn(Optional.of(seller));
        when(storyRepository.save(any(Story.class))).thenReturn(story);

        StoryResponseDTO response = storyService.toggleLikeOrDislike(1L, "dislike");

        assertEquals(0L, response.story().getLikeCount());
        assertEquals(1L, response.story().getDislikeCount());

        verify(userService).getByEmail(anyString());
        verify(likeRepository).getByStoryIdAndUserId(anyLong(), anyLong());
        verify(likeRepository).save(any(Like.class));
        verify(storyRepository).findById(anyLong());
        verify(storyRepository).save(any(Story.class));
    }

    @Test
    void testToggleLikeOrDislikeFirstClickLikeThenClickLike() throws AuthException {
        story.setLikeCount(1L);
        when(userService.getByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.getByStoryIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(like));
        when(storyRepository.findById(anyLong())).thenReturn(Optional.of(story));
        when(sellerRepository.findById(anyLong())).thenReturn(Optional.of(seller));
        when(storyRepository.save(any(Story.class))).thenReturn(story);

        StoryResponseDTO response = storyService.toggleLikeOrDislike(1L, "like");

        assertEquals(0L, response.story().getLikeCount());
        assertEquals(0L, response.story().getDislikeCount());

        verify(userService).getByEmail(anyString());
        verify(likeRepository).getByStoryIdAndUserId(anyLong(), anyLong());
        verify(storyRepository).findById(anyLong());
        verify(storyRepository).save(any(Story.class));
    }

    @Test
    void testToggleLikeOrDislikeFirstClickDislikeThenClickLike() throws AuthException {
        story.setDislikeCount(1L);
        when(userService.getByEmail("test@example.com")).thenReturn(user);
        when(likeRepository.getByStoryIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(dislike));
        when(storyRepository.findById(anyLong())).thenReturn(Optional.of(story));
        when(sellerRepository.findById(anyLong())).thenReturn(Optional.of(seller));
        when(storyRepository.save(any(Story.class))).thenReturn(story);

        StoryResponseDTO response = storyService.toggleLikeOrDislike(1L, "like");

        assertEquals(1L, response.story().getLikeCount());
        assertEquals(0L, response.story().getDislikeCount());

        verify(userService).getByEmail(anyString());
        verify(likeRepository).getByStoryIdAndUserId(anyLong(), anyLong());
        verify(storyRepository).findById(anyLong());
        verify(storyRepository).save(any(Story.class));
    }

    @Test
    void testUpdate() throws IOException, AuthException {
        var mainContentResponse = new CloudinaryUploadResponse("New video url", "publicId");
        var logoResponse = new CloudinaryUploadResponse("New photo url", "publicId");

        when(cloudinaryService.uploadFile(mainContent, "story")).thenReturn(mainContentResponse);
        when(cloudinaryService.uploadFile(logo, "story")).thenReturn(logoResponse);
        when(storyRepository.findById(anyLong())).thenReturn(Optional.of(story));
        when(storyRepository.save(any(Story.class))).thenReturn(story);
        when(sellerRepository.findById(anyLong())).thenReturn(Optional.of(seller));

        storyService.update(1L, 1L, "New desc", mainContent, logo);

        verify(cloudinaryService, times(2)).deleteFile(anyString());
        verify(storyRepository).findById(anyLong());
        verify(storyRepository).save(any(Story.class));
        verify(sellerRepository).findById(anyLong());
    }

    @Test
    void testDelete() throws AuthException {
        when(storyRepository.findById(anyLong())).thenReturn(Optional.of(story));

        storyService.delete(1L, 1L);

        verify(storyRepository).deleteById(anyLong());
    }
}
