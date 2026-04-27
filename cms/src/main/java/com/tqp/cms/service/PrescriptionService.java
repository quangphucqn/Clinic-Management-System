package com.tqp.cms.service;

import com.tqp.cms.dto.response.PrescriptionResponse;
import com.tqp.cms.dto.response.PageResponse;
import java.time.LocalDate;
import java.util.UUID;

public interface PrescriptionService {
    PrescriptionResponse getPrescriptionById(UUID prescriptionId);

    PrescriptionResponse getPrescriptionByMedicalRecordId(UUID medicalRecordId);

    PageResponse<PrescriptionResponse> getMyPrescriptions(LocalDate issuedDate, int page, int size);
}
