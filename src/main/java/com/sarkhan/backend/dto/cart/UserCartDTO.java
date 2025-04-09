package com.sarkhan.backend.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserCartDTO {
    private List<CartItemResponseDTO> items;
    private double totalPrice;
}
