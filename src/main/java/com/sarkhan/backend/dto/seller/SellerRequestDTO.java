package com.sarkhan.backend.dto.seller;

import jakarta.validation.constraints.NotBlank;

public record SellerRequestDTO(@NotBlank String fullName,
                               @NotBlank String brandName,
                               @NotBlank String brandEmail,
                               @NotBlank String brandVOEN,

                               @NotBlank String fatherName,
                               @NotBlank String finCode,
                               @NotBlank String brandPhone) {
}
