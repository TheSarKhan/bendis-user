package com.sarkhan.backend.service.impl.product.util;

import com.sarkhan.backend.model.product.Product;
import com.sarkhan.backend.model.product.items.ProductUserHistory;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.product.items.ProductUserHistoryRepository;
import com.sarkhan.backend.service.UserService;
import jakarta.security.auth.message.AuthException;
import org.slf4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    public static User getCurrentUser(UserService userService, Logger log) throws AuthException {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (email == null) {
            log.warn("User doesn't login!!!");
            throw new AuthException("User doesn't login!!!");
        }
        return userService.getByEmail(email);
    }

    public static void addProductUserHistory(Product product, UserService userService, ProductUserHistoryRepository historyRepository, Logger log) {
        try {
            User user = getCurrentUser(userService, log);
            ProductUserHistory history = ProductUserHistory.builder()
                    .userId(user.getId())
                    .subCategoryId(product.getSubCategoryId())
                    .build();
            historyRepository.save(history);
        } catch (AuthException ignored) {
        }
    }
}

