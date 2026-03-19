package com.tqp.cms.controller;

import com.nimbusds.jose.JOSEException;
import com.tqp.cms.dto.request.AuthenticationRequest;
import com.tqp.cms.dto.request.IntrospectRequest;
import com.tqp.cms.dto.response.ApiResponse;
import com.tqp.cms.dto.response.AuthenticationResponse;
import com.tqp.cms.dto.response.IntrospectResponse;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.service.AuthenticationService;
import jakarta.validation.Valid;
import java.text.ParseException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .result(authenticationService.authenticate(request))
                .build();
    }

    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<IntrospectResponse>> introspect(@RequestBody @Valid IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        ApiResponse<IntrospectResponse> response = ApiResponse.<IntrospectResponse>builder()
                .code(result.isValid() ? ErrorCode.SUCCESS.getCode() : ErrorCode.UNAUTHENTICATED.getCode())
                .message(result.isValid() ? "Token is valid" : "Token is invalid")
                .result(result)
                .build();
        return ResponseEntity.status(result.isValid() ? HttpStatus.OK : HttpStatus.UNAUTHORIZED).body(response);
    }
}
