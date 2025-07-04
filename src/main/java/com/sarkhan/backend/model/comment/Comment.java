package com.sarkhan.backend.model.comment;

import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.user.User;
import jakarta.persistence.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(optional = false)
//    private User user;
    private long userId;

//    @ManyToOne(optional = false)
//    private Product product;
    private long productId;

    @Column(nullable = false, length = 300)
    private String text;

    @Min(1)
    @Max(5)
    private int rating;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
//