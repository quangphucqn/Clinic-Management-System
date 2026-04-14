package com.tqp.cms.mapper;

import com.tqp.cms.dto.response.PatientAppoinmentDoctorResponse;
import com.tqp.cms.entity.Patient;
import com.tqp.cms.entity.Users;
import org.springframework.stereotype.Component;


@Component
public class PatientMapper {

    public PatientAppoinmentDoctorResponse toSearchResponse(Patient patient) {

        return PatientAppoinmentDoctorResponse.builder()
                .patientId(patient.getId())
                .fullName(patient.getUserAccount().getFullName())
                .email(patient.getUserAccount().getEmail())
                .build();
    }
}