package com.tqp.cms.dto.response;

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
public class DoctorProfileResponse {
    UUID doctorId;
    UUID specialtyId;
    String specialtyName;
    String licenseNumber;
    String roomNumber;
    Integer yearsOfExperience;
    String biography;
}
