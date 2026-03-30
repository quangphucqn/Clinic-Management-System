package com.tqp.cms.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorDetailResponse {
    UUID id;
    UUID userId;
    String username;
    String fullName;
    String email;
    String phoneNumber;
    UUID specialtyId;
    String specialtyName;
    String licenseNumber;
    String roomNumber;
    Integer yearsOfExperience;
    String biography;
    boolean active;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
