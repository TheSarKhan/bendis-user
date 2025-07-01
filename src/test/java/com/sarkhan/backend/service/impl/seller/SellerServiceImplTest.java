package com.sarkhan.backend.service.impl.seller;

import com.sarkhan.backend.dto.seller.SellerRequestDTO;
import com.sarkhan.backend.dto.seller.SellerResponseDTO;
import com.sarkhan.backend.dto.seller.UpdateSellerRequestDto;
import com.sarkhan.backend.exception.DataNotFoundException;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.mapper.seller.SellerMapper;
import com.sarkhan.backend.model.user.Seller;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.seller.SellerRepository;
import com.sarkhan.backend.repository.user.UserRepository;
import com.sarkhan.backend.service.impl.SellerServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SellerServiceImplTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SellerRepository sellerRepository;
    @Mock
    private SellerMapper sellerMapper;
    @InjectMocks
    private SellerServiceImpl sellerService;

    private SellerRequestDTO sellerRequestDTO;
    private Seller seller;
    private User user;
    private UpdateSellerRequestDto updateSellerRequestDto;
    private SellerResponseDTO responseDTO;

    @BeforeEach
    public void setUp() {
        sellerRequestDTO = new SellerRequestDTO("Name Surname", "Brand", "mybrand@gmail.com",
                "voen", "Father name", "finCode123", "+994559281029");
        updateSellerRequestDto = new UpdateSellerRequestDto(
                "Name Surname", "Brand", "mybrand@gmail.com",
                "voen", "Father name", "finCode123", "+994559281029");
        responseDTO = new SellerResponseDTO(
                "Name Surname", "Brand", "mybrand@gmail.com",
                "voen", "Father name", "finCode123", "+994559281029");
        user = new User();
        user.setEmail("user@gmail.com");
        user.setFullName("Name Surname");
        user.setPhoneNumber("+994559281029");
        seller = new Seller();
        user.setSeller(seller);
    }

    @AfterEach
    public void tearDown() {
        sellerService = null;
        sellerRequestDTO = null;
        user = null;
        updateSellerRequestDto = null;
    }

    @Test
    void testCreateSeller_success() throws DataNotFoundException {
        when(jwtService.extractEmail(anyString())).thenReturn("user@gmail.com");
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        when(sellerMapper.sellerRequestDtoToSeller(sellerRequestDTO)).thenReturn(seller);
        when(sellerMapper.sellerToSellerResponseDto(any())).thenReturn(responseDTO);
        SellerResponseDTO response = sellerService.createSeller(sellerRequestDTO, "Bearer token");

        assertNotNull(response);
        verify(userRepository).save(user);
        assertEquals(seller.getBrandName(), "Brand");
    }

    @Test
    void testCreateSeller_UserNotFound() {
        when(jwtService.extractEmail(anyString())).thenReturn("notfound@example.com");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> sellerService.createSeller(sellerRequestDTO, "Bearer token"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetById_Success() throws DataNotFoundException {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        SellerResponseDTO responseDTO = new SellerResponseDTO(
                "Name Surname", "Brand", "mybrand@gmail.com",
                "voen", "Father name", "finCode123", "+994559281029");
        when(sellerMapper.sellerToSellerResponseDto(seller)).thenReturn(responseDTO);
        SellerResponseDTO result = sellerService.getById(1L);
        assertNotNull(result);
    }

    @Test
    void testGetById_NotFound() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> sellerService.getById(1L));
    }

    @Test
    void testUpdateSeller_Success() throws DataNotFoundException {
        when(jwtService.extractEmail(anyString())).thenReturn("user@gmail.com");
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        SellerResponseDTO responseDTO = new SellerResponseDTO(
                "Name Surname", "Brand", "mybrand@gmail.com",
                "voen", "Father name", "finCode123", "+994559281029");
        when(sellerMapper.sellerToSellerResponseDto(seller)).thenReturn(responseDTO);

        SellerResponseDTO response = sellerService.update(updateSellerRequestDto, "Bearer token");
        assertNotNull(response);
        verify(userRepository).save(user);

    }

    @Test
    void testUpdateSeller_NotFound() {
        when(jwtService.extractEmail(anyString())).thenReturn("userNotFound@gmail.com");
        when(userRepository.findByEmail("userNotFound@gmail.com")).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> sellerService.update(updateSellerRequestDto, "Bearer token"));
    }
}
