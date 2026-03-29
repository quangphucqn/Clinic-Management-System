package com.tqp.cms.mapper;

import com.tqp.cms.dto.request.UnitCreationRequest;
import com.tqp.cms.dto.response.UnitResponse;
import com.tqp.cms.entity.Unit;
import org.springframework.stereotype.Component;

@Component
public class UnitMapper {
    public Unit toEntity(UnitCreationRequest request) {
        return Unit.builder()
                .name(request.getName().trim())
                .build();
    }

    public UnitResponse toResponse(Unit unit) {
        return UnitResponse.builder()
                .id(unit.getId())
                .name(unit.getName())
                .build();
    }
}
