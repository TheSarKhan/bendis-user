package com.sarkhan.backend.dto.order;

import com.sarkhan.backend.dto.address.AddressRequestDto;
import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long orderId;
    private List<CartItemRequestDTO> items;
    private Double totalPrice;
    private AddressRequestDto addressRequestDto;
}
