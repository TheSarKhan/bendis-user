package com.sarkhan.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    private String subject;
    private String text;
    // Əlavə olaraq istəsən:
    // private boolean isHtml; // HTML formatında olub-olmaması
    // private String priority; // Məsələn, "HIGH", "LOW"
}