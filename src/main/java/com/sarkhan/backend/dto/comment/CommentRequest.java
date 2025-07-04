package com.sarkhan.backend.dto.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {

    private Long productId;  // hansı məhsula comment yazılır
    private String text;     // commentin mətn hissəsi
    private int rating;      // qiymətləndirmə

    // Getter və Setter-lər (ya Lombok @Data istifadə edə bilərsən)

}



