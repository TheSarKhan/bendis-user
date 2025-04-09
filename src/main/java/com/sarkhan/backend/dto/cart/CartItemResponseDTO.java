package com.sarkhan.backend.dto.cart;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponseDTO {
    private Long productId;
    private int quantity;
    private double totalPrice;
}
