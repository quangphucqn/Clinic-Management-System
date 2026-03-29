package com.tqp.cms.service;

import com.tqp.cms.dto.request.MedicalRecordRequest;
import com.tqp.cms.dto.request.PrescriptionDoctorRequest;
import com.tqp.cms.dto.response.MedicalRecordResponse;
import com.tqp.cms.dto.response.PrescriptionDoctorResponse;

public interface PrescriptionDoctorService {

    PrescriptionDoctorResponse createPrescription(PrescriptionDoctorRequest request);
}
