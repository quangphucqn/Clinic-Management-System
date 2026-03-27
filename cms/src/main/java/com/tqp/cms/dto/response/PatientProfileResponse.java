package com.tqp.cms.dto.response;

import com.tqp.cms.entity.Gender;
import java.time.LocalDate;
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
public class PatientProfileResponse {
    UUID patientId;
    Gender gender;
    LocalDate dateOfBirth;
    String address;
    String emergencyContactName;
    String emergencyContactPhone;
}
