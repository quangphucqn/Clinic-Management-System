package com.tqp.cms.service;

import com.tqp.cms.dto.request.UserCreationRequest;
import com.tqp.cms.dto.request.UserUpdateRequest;
import com.tqp.cms.dto.response.CurrentUserProfileResponse;
import com.tqp.cms.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);

    Page<UserResponse> getUsers(int page, int size, String username);

    UserResponse getUserById(UUID userId);

    CurrentUserProfileResponse getCurrentUserProfile();

    UserResponse updateUser(UUID userId, UserUpdateRequest request);

    void softDeleteUser(UUID userId);
}
