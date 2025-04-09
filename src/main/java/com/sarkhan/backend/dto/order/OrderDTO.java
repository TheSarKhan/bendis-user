package com.sarkhan.backend.dto.order;

import com.sarkhan.backend.dto.cart.CartItemRequestDTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private List<CartItemRequestDTO> items;
    private Double totalPrice;
}
