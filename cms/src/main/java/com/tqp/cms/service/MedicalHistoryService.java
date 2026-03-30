package com.tqp.cms.service;

import com.tqp.cms.dto.request.MedicalHistoryRequest;
import com.tqp.cms.dto.response.MedicalHistoryResponse;
import com.tqp.cms.dto.response.PageResponse;

import java.util.List;
import java.util.UUID;

public interface MedicalHistoryService {
    PageResponse<MedicalHistoryResponse> getPatientHistory(
            UUID patientId,
            MedicalHistoryRequest request
    );
}
