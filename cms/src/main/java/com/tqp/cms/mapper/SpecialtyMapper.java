package com.tqp.cms.mapper;

import com.tqp.cms.dto.request.SpecialtyCreationRequest;
import com.tqp.cms.dto.response.SpecialtyResponse;
import com.tqp.cms.entity.Specialty;
import org.springframework.stereotype.Component;

@Component
public class SpecialtyMapper {
    public Specialty toEntity(SpecialtyCreationRequest request) {
        return Specialty.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public SpecialtyResponse toResponse(Specialty specialty) {
        return SpecialtyResponse.builder()
                .id(specialty.getId())
                .name(specialty.getName())
                .description(specialty.getDescription())
                .build();
    }
}
