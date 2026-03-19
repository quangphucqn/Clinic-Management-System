package com.tqp.cms.exception;

import com.tqp.cms.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(basePackages = "com.tqp.cms")
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.<Void>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getHttpStatus()).body(
                ApiResponse.<Void>builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        if (fieldError == null) {
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .code(ErrorCode.VALIDATION_ERROR.getCode())
                    .message(ErrorCode.VALIDATION_ERROR.getMessage())
                    .build());
        }

        String enumKey = fieldError.getDefaultMessage();
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        try {
            if (enumKey != null) {
                errorCode = ErrorCode.valueOf(enumKey);
            }
        } catch (IllegalArgumentException ignored) {
            // Fall back to a generic validation error if message key is not an enum name.
        }

        return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    ResponseEntity<ApiResponse<Void>> handlingConstraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                .code(ErrorCode.VALIDATION_ERROR.getCode())
                .message(exception.getMessage())
                .build());
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    ResponseEntity<ApiResponse<Void>> handlingMissingServletRequestParameterException(
            MissingServletRequestParameterException exception
    ) {
        String parameterName = exception.getParameterName();
        String parameterType = exception.getParameterType();

        return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                .code(ErrorCode.FIELD_REQUIRED.getCode())
                .message("Required parameter '%s' of type '%s' is missing".formatted(parameterName, parameterType))
                .build());
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Void>> handlingException(Exception exception) {
        log.error("Unhandled exception", exception);
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }
}
