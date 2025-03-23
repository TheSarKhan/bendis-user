package com.sarkhan.backend.dto.authorization;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SellerRequest {
    String brandName;
    String brandVOEN;
    String fatherName;
}
