package com.tqp.cms.controller;

import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.UserResponse;
import com.tqp.cms.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get users successfully")
                .result(userService.getUsers())
                .build();
    }
}
