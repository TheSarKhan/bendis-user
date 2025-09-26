package com.sarkhan.backend.mapper.seller;

import com.sarkhan.backend.dto.seller.SellerRequestDTO;
import com.sarkhan.backend.dto.seller.SellerResponseDTO;
import com.sarkhan.backend.model.seller.Seller;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SellerMapper {

    SellerResponseDTO sellerToSellerResponseDto(Seller seller);

    List<SellerResponseDTO> sellersToSellersResponseDto(List<Seller> sellers);

    Seller sellerRequestDtoToSeller(SellerRequestDTO sellerRequestDTO);

}
