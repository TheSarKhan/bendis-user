package com.sarkhan.backend.service.impl;

import com.sarkhan.backend.dto.header.HeaderResponse;
import com.sarkhan.backend.dto.user.UserResponse;
import com.sarkhan.backend.mapper.user.UserMapper;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.service.HeaderService;
import com.sarkhan.backend.service.UserService;
import com.sarkhan.backend.service.impl.product.util.UserUtil;
import com.sarkhan.backend.service.product.items.CategoryService;
import com.sarkhan.backend.service.product.items.SubCategoryService;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeaderServiceImpl implements HeaderService {
    private final UserService userService;

    private final CategoryService categoryService;

    private final SubCategoryService subCategoryService;

    @Override
    public HeaderResponse getHeader() {
        UserResponse userResponse = null;
        try {
            userResponse = UserMapper.mapUserToUserResponse(UserUtil.getCurrentUser(userService, log));
        } catch (AuthException ignored) {}
        return new HeaderResponse(userResponse, categoryService.getAll(), subCategoryService.getAll());
    }
}
