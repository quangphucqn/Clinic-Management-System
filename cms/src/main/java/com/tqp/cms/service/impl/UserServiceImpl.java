package com.tqp.cms.service.impl;

import com.tqp.cms.dto.response.UserResponse;
import com.tqp.cms.entity.Users;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.UserService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepository usersRepository;

    @Override
    public List<UserResponse> getUsers() {
        return usersRepository.findAll().stream()
                .sorted(Comparator.comparing(Users::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(Users users) {
        return UserResponse.builder()
                .id(users.getId())
                .username(users.getUsername())
                .fullName(users.getFullName())
                .email(users.getEmail())
                .phoneNumber(users.getPhoneNumber())
                .role(users.getRole())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .build();
    }
}
