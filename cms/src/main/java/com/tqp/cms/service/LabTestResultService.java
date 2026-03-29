package com.tqp.cms.service;

import com.tqp.cms.dto.request.LabTestResultRequest;
import com.tqp.cms.entity.LabTestResult;

public interface LabTestResultService {
    LabTestResult createLabTestResult(LabTestResultRequest request);
}
