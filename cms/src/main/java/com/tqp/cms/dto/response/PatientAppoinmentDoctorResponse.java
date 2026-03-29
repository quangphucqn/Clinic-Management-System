package com.tqp.cms.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientAppoinmentDoctorResponse {
    String fullName;
    String numberPhone;
}
