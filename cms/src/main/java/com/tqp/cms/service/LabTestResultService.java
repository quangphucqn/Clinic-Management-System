package com.tqp.cms.service;

import com.tqp.cms.dto.request.LabTestResultRequest;
import com.tqp.cms.dto.response.PatientLabResultResponse;
import com.tqp.cms.entity.LabTestResult;
import java.util.List;

public interface LabTestResultService {
    LabTestResult createLabTestResult(LabTestResultRequest request);

    List<PatientLabResultResponse> getMyLabResults();
}
