package com.sarkhan.backend.dto.cloudinary;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CloudinaryUploadResponse {
    private String url;
    private String publicId;
}
