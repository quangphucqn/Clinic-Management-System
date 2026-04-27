package com.tqp.cms.service;

import com.tqp.cms.dto.request.DoctorReviewCreationRequest;
import com.tqp.cms.dto.response.DoctorReviewResponse;
import com.tqp.cms.dto.response.PageResponse;
import com.tqp.cms.dto.response.PatientDoctorReviewResponse;

public interface DoctorReviewService {
    DoctorReviewResponse createReview(DoctorReviewCreationRequest request);

    PageResponse<PatientDoctorReviewResponse> getMyReviews(int page, int size);
}
