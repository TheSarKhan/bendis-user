package com.sarkhan.backend.dto.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {

    Long id;
    String userFullName;
    Long productId;
    Long userId;

    String text;
    int rating;
    LocalDateTime createdAt;

}
