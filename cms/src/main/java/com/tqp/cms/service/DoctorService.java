package com.tqp.cms.service;

import com.tqp.cms.dto.request.DoctorCreationRequest;
import com.tqp.cms.dto.request.DoctorUpdateRequest;
import com.tqp.cms.dto.response.DoctorDetailResponse;
import com.tqp.cms.dto.response.DoctorResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface DoctorService {
    DoctorDetailResponse createDoctor(DoctorCreationRequest request);

    Page<DoctorResponse> getDoctors(int page, int size, String keyword);

    DoctorDetailResponse getDoctorById(UUID doctorId);

    DoctorDetailResponse updateDoctor(UUID doctorId, DoctorUpdateRequest request);

    void softDeleteDoctor(UUID doctorId);
}
