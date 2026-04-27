package com.tqp.cms.dto.response;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PatientAppoinmentDoctorResponse {
    UUID patientId;
    String fullName;
    String email;
}
