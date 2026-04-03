package com.tqp.cms.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorResponse {
    UUID id;
    UUID userId;
    String fullName;
    UUID specialtyId;
    String specialtyName;
    String roomNumber;
    String licenseNumber;
    Integer yearsOfExperience;
}
