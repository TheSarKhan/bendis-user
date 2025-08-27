package com.sarkhan.backend.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemRequestDTO {

    @NotNull(message = "product id can not be null")
    private Long productId;

    @NotNull(message = "quantity can not be null")
    private Integer quantity;

    private String color;
    private String size;

    private BigDecimal totalPrice;
}