package com.tqp.cms.controller;

import com.tqp.cms.dto.request.UserCreationRequest;
import com.tqp.cms.dto.request.UserUpdateRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.UserResponse;
import com.tqp.cms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<UserResponse>builder()
                        .code(HttpStatus.OK.value())
                        .message("User created successfully")
                        .result(userService.createUser(request))
                        .build()
        );
    }

    @GetMapping
    public ApiResponse<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username
    ) {
        return ApiResponse.<Page<UserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get users successfully")
                .result(userService.getUsers(page, size, username))
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserById(@PathVariable UUID userId) {
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Get user successfully")
                .result(userService.getUserById(userId))
                .build();
    }

    @PatchMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable UUID userId,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("User updated successfully")
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteUser(@PathVariable UUID userId) {
        userService.softDeleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.OK.value())
                        .message("Deleted")
                        .build()
        );
    }
}
