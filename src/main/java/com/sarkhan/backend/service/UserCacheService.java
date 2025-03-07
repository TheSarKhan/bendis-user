package com.sarkhan.backend.service;

import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public Optional<User> findByEmail(String email) {
        String cacheKey = "user:" + email;
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);

        if (cachedUser != null) {
            return Optional.of(cachedUser);
        }

        Optional<User> user = userRepository.findByEmail(email);
        user.ifPresent(u -> redisTemplate.opsForValue().set(cacheKey, u, 1, TimeUnit.HOURS));
        return user;
    }
}
