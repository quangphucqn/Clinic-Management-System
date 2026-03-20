package com.tqp.cms.service;

import com.tqp.cms.dto.request.SpecialtyCreationRequest;
import com.tqp.cms.dto.request.SpecialtyUpdateRequest;
import com.tqp.cms.dto.response.SpecialtyResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface SpecialtyService {
    SpecialtyResponse createSpecialty(SpecialtyCreationRequest request);

    SpecialtyResponse getSpecialtyById(UUID specialtyId);

    Page<SpecialtyResponse> getSpecialties(int page, int size, String name);

    SpecialtyResponse updateSpecialty(UUID specialtyId, SpecialtyUpdateRequest request);

    void softDeleteSpecialty(UUID specialtyId);
}
