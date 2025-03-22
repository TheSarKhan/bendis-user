package com.sarkhan.backend.model.product.items;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    String header;
    String description;
    String iconUrl;
}
