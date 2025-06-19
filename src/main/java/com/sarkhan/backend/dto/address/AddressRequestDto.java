package com.sarkhan.backend.dto.address;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequestDto {
    @NotBlank
    String finCode;
    @NotBlank
    String region;
    @NotBlank
    String street;
    @NotBlank
    String city;
    @NotBlank
    String postalCode;
}
