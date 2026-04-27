package com.tqp.cms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    SUCCESS(200, "Success", HttpStatus.OK),
    DELETED(204, "Deleted", HttpStatus.NO_CONTENT),

    UNCATEGORIZED_EXCEPTION(500, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(400, "Validation error", HttpStatus.BAD_REQUEST),
    INVALID_KEY(400, "Invalid message key", HttpStatus.BAD_REQUEST),
    FIELD_REQUIRED(400, "Required field is missing", HttpStatus.BAD_REQUEST),
    BAD_REQUEST(400, "Bad request", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND(404, "Resource not found", HttpStatus.NOT_FOUND),

    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "You do not have permission", HttpStatus.FORBIDDEN),

    USER_EXISTED(409, "User already exists", HttpStatus.CONFLICT),
    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(409, "Email already exists", HttpStatus.CONFLICT),
    CURRENT_PASSWORD_INCORRECT(400, "Current password is incorrect", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_OLD(400, "New password must be different from current password", HttpStatus.BAD_REQUEST),
    PATIENT_NOT_FOUND(404, "Patient not found", HttpStatus.NOT_FOUND),
    DOCTOR_NOT_FOUND(404, "Doctor not found", HttpStatus.NOT_FOUND),
    DOCTOR_EXISTED(409, "Doctor already exists", HttpStatus.CONFLICT),
    MEDICAL_RECORD_EXISTED(409, "Medical already exists", HttpStatus.CONFLICT),
    PRESCRIPTION_EXISTED(409, "prescription already exists", HttpStatus.CONFLICT),
    SPECIALTY_NOT_FOUND(404, "Specialty not found", HttpStatus.NOT_FOUND),
    SPECIALTY_EXISTED(409, "Specialty already exists", HttpStatus.CONFLICT),
    UNIT_NOT_FOUND(404, "Unit not found", HttpStatus.NOT_FOUND),
    UNIT_EXISTED(409, "Unit already exists", HttpStatus.CONFLICT),
    MEDICINE_NOT_FOUND(404, "Medicine not found", HttpStatus.NOT_FOUND),
    MEDICINE_EXISTED(409, "Medicine already exists", HttpStatus.CONFLICT),
    MEDICINE_IMAGE_UPLOAD_FAILED(500, "Failed to upload medicine image", HttpStatus.INTERNAL_SERVER_ERROR),
    SLOT_NOT_FOUND(404, "Time slot not found", HttpStatus.NOT_FOUND),
    SLOT_EXISTED(409, "Time slot already exists", HttpStatus.CONFLICT),
    SLOT_TIME_INVALID(400, "Start time must be before end time", HttpStatus.BAD_REQUEST),
    SLOT_DISABLED(400, "Time slot is disabled", HttpStatus.BAD_REQUEST),
    SLOT_FULL(409, "Time slot is full", HttpStatus.CONFLICT),
    APPOINTMENT_NOT_FOUND(404, "Appointment not found", HttpStatus.NOT_FOUND),
    APPOINTMENT_DUPLICATED(409, "Appointment already exists for this slot", HttpStatus.CONFLICT),
    APPOINTMENT_STATUS_INVALID(400, "Appointment status is invalid for this operation", HttpStatus.BAD_REQUEST),
    MEDICAL_RECORD_NOT_FOUND(404, "Medical record not found", HttpStatus.NOT_FOUND),
    PRESCRIPTION_NOT_FOUND(404, "Prescription not found", HttpStatus.NOT_FOUND),
    LAB_TEST_ORDER_NOT_FOUND(404, "Lab test order not found", HttpStatus.NOT_FOUND),
    LAB_TEST_RESULT_NOT_FOUND(404, "Lab test result not found", HttpStatus.NOT_FOUND),
    LAB_TEST_RESULT_EXISTED(409, "Lab test result already exists", HttpStatus.CONFLICT),
    PAYMENT_NOT_FOUND(404, "Payment transaction not found", HttpStatus.NOT_FOUND),
    PAYMENT_FAILED(400, "Payment failed", HttpStatus.BAD_REQUEST),
    PAYMENT_METHOD_NOT_SUPPORTED(400, "Only MoMo payment is supported for appointment booking", HttpStatus.BAD_REQUEST),
    PAYMENT_EXPIRED(400, "Cannot process payment because appointment date has passed", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND(404, "Doctor review not found", HttpStatus.NOT_FOUND),
    REVIEW_EXISTED(409, "Doctor review already exists", HttpStatus.CONFLICT),
    NOTIFICATION_NOT_FOUND(404, "Notification not found", HttpStatus.NOT_FOUND),
    NOTIFICATION_TARGET_REQUIRED(400, "Target user or target role is required", HttpStatus.BAD_REQUEST),
    SENDGRID_NOT_CONFIGURED(400, "SendGrid is not configured", HttpStatus.BAD_REQUEST),
    EMAIL_SEND_FAILED(500, "Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR);

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
