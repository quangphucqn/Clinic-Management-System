package com.tqp.cms.service;

import com.tqp.cms.dto.request.MedicalRecordRequest;
import com.tqp.cms.dto.response.AppointmentDoctorDetailsResponse;
import com.tqp.cms.dto.response.MedicalRecordDetailResponse;
import com.tqp.cms.dto.response.MedicalRecordResponse;
import com.tqp.cms.entity.MedicalRecord;

import java.util.UUID;

public interface MedicalRecordService {
    MedicalRecordResponse createMedicalRecord(MedicalRecordRequest request);

    MedicalRecordDetailResponse getMedicalRecordById(UUID medicalRecordId );
}
