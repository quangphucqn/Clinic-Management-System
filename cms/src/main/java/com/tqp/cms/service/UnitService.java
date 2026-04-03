package com.tqp.cms.service;

import com.tqp.cms.dto.request.UnitCreationRequest;
import com.tqp.cms.dto.request.UnitUpdateRequest;
import com.tqp.cms.dto.response.UnitResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UnitService {
    UnitResponse createUnit(UnitCreationRequest request);

    Page<UnitResponse> getUnits(int page, int size, String name);

    UnitResponse updateUnit(UUID unitId, UnitUpdateRequest request);

    void softDeleteUnit(UUID unitId);
}
