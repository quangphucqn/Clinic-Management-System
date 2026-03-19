package com.tqp.cms.dto.response;

import com.tqp.cms.entity.UserRole;
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
public class PatientRegistrationResponse {
    UUID userId;
    UUID patientId;
    String username;
    String fullName;
    String email;
    UserRole role;
}
