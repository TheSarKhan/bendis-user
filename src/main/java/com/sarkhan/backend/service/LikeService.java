package com.sarkhan.backend.service;


import java.util.Optional;

public interface LikeService {

    String addAndRemoveLike(String storyId, String userId);

    Optional<Long> getLike(String storyId);

}
