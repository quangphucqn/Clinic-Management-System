package com.tqp.cms.mapper;

import com.tqp.cms.dto.request.UserCreationRequest;
import com.tqp.cms.dto.response.UserResponse;
import com.tqp.cms.entity.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public Users toEntity(UserCreationRequest request) {
        return Users.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .build();
    }

    public UserResponse toResponse(Users user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
