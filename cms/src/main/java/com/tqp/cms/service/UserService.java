package com.tqp.cms.service;

import com.tqp.cms.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> getUsers();
}
