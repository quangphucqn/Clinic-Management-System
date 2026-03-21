package com.tqp.cms.service;

import com.tqp.cms.dto.response.PrescriptionResponse;
import java.util.UUID;

public interface PrescriptionService {
    PrescriptionResponse getPrescriptionById(UUID prescriptionId);

    PrescriptionResponse getPrescriptionByMedicalRecordId(UUID medicalRecordId);
}
