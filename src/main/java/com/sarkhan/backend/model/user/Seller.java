package com.sarkhan.backend.model.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seller {

    String nameAndSurname;
    String brandName;
    String brandEmail;
    String brandVOEN;
    String fatherName;
    String finCode;
    String brandPhone;

}

