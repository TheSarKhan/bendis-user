package com.sarkhan.backend.model.product.items;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pluses")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(nullable = false, unique = true)
    String header;

    String description;

    @Column(name = "icon_url")
    String iconUrl;
}
