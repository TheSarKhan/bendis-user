package com.sarkhan.backend.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequestDTO {

    @NotNull(message = "product id can not be null")
    private Long productId;
    private String color;
    @NotNull(message = "quantity can not be null")
    private Integer quantity;
}
