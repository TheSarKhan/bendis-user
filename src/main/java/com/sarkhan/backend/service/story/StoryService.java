package com.sarkhan.backend.service.story;

import com.sarkhan.backend.dto.story.StoryResponseDTO;
import com.sarkhan.backend.exception.DataNotFoundException;
import com.sarkhan.backend.model.story.Story;
import jakarta.security.auth.message.AuthException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StoryService {
    StoryResponseDTO create(Long sellerId,
                            String description,
                            MultipartFile mainContent,
                            MultipartFile logo) throws IOException, DataNotFoundException;

    List<Story> getAll();

    List<Story> getForHomePage();

    List<Story> getBySellerId(Long sellerId);

    StoryResponseDTO getByIdAndAddView(Long id);

    StoryResponseDTO update(Long sellerId,
                            Long id,
                            String description,
                            MultipartFile mainContent,
                            MultipartFile logo) throws IOException, AuthException;

    void delete(Long sellerId, Long id) throws AuthException;
}
