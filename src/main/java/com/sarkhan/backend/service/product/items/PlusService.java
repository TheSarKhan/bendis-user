package com.sarkhan.backend.service.product.items;

import com.sarkhan.backend.model.product.items.Plus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PlusService {
    Plus add(String header, String description, MultipartFile icon) throws IOException;

    List<Plus> getAll();

    Plus getById(Long id);

    Plus getByHeader(String header);

    Plus update(Long id, String header, String description, MultipartFile icon);

    void delete(Long id);
}
