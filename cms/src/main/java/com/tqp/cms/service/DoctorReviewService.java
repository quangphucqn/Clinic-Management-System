package com.tqp.cms.service;

import com.tqp.cms.dto.request.DoctorReviewCreationRequest;
import com.tqp.cms.dto.response.DoctorReviewResponse;

public interface DoctorReviewService {
    DoctorReviewResponse createReview(DoctorReviewCreationRequest request);
}
