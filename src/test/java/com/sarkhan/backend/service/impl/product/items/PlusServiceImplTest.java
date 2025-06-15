package com.sarkhan.backend.service.impl.product.items;

import com.sarkhan.backend.dto.cloudinary.CloudinaryUploadResponse;
import com.sarkhan.backend.model.product.items.Plus;
import com.sarkhan.backend.repository.product.items.PlusRepository;
import com.sarkhan.backend.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlusServiceImplTest {

    @Mock
    private PlusRepository plusRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private PlusServiceImpl plusService;

    private Plus samplePlus;

    @BeforeEach
    void setUp() {
        samplePlus = Plus.builder()
                .id(1L)
                .header("Old Header")
                .description("Old Description")
                .iconUrl("old-url")
                .build();
    }

    @Test
    void testAdd() throws IOException {
        CloudinaryUploadResponse response = new CloudinaryUploadResponse("new-url","publicId");
        when(cloudinaryService.uploadFile(multipartFile, "icon")).thenReturn(response);
        when(plusRepository.save(any(Plus.class))).thenAnswer(inv -> inv.getArgument(0));

        Plus result = plusService.add("New Header", "New Description", multipartFile);

        assertEquals("New Header", result.getHeader());
        assertEquals("New Description", result.getDescription());
        assertEquals("new-url", result.getIconUrl());

        verify(cloudinaryService).uploadFile(multipartFile, "icon");
        verify(plusRepository).save(any(Plus.class));
    }

    @Test
    void testGetAll() {
        when(plusRepository.findAll()).thenReturn(List.of(samplePlus));

        List<Plus> result = plusService.getAll();
        assertEquals(1, result.size());
        assertEquals("Old Header", result.getFirst().getHeader());
    }

    @Test
    void testGetById_whenExists() {
        when(plusRepository.findById(1L)).thenReturn(Optional.of(samplePlus));

        Plus result = plusService.getById(1L);

        assertEquals(samplePlus, result);
        verify(plusRepository).findById(1L);
    }

    @Test
    void testGetById_whenNotFound() {
        when(plusRepository.findById(2L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(NoSuchElementException.class, () -> plusService.getById(1L));
        assertTrue(ex.getMessage().contains("Cannot find any plus with 2 id."));
        verify(plusRepository).findById(2L);
    }

    @Test
    void testUpdate() throws IOException {
        CloudinaryUploadResponse response = new CloudinaryUploadResponse("updated-url","PublicId");

        when(plusRepository.findById(1L)).thenReturn(Optional.of(samplePlus));
        when(cloudinaryService.uploadFile(multipartFile, "icon")).thenReturn(response);
        when(plusRepository.save(any(Plus.class))).thenAnswer(inv -> inv.getArgument(0));

        Plus updated = plusService.update(1L, "Updated Header", "Updated Description", multipartFile);

        assertEquals("Updated Header", updated.getHeader());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals("updated-url", updated.getIconUrl());

        verify(cloudinaryService).deleteFile("old-url");
        verify(cloudinaryService).uploadFile(multipartFile, "icon");
        verify(plusRepository).save(samplePlus);
    }

    @Test
    void testDelete() {
        doNothing().when(plusRepository).deleteById(1L);
        plusService.delete(1L);
        verify(plusRepository).deleteById(1L);
    }
}
