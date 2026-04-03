package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.UnitCreationRequest;
import com.tqp.cms.dto.request.UnitUpdateRequest;
import com.tqp.cms.dto.response.UnitResponse;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.UnitMapper;
import com.tqp.cms.repository.UnitRepository;
import com.tqp.cms.service.UnitService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UnitServiceImpl implements UnitService {
    UnitRepository unitRepository;
    UnitMapper unitMapper;

    @Override
    @Transactional
    public UnitResponse createUnit(UnitCreationRequest request) {
        String normalizedName = request.getName().trim();
        if (unitRepository.existsByName(normalizedName)) {
            throw new AppException(ErrorCode.UNIT_EXISTED);
        }
        var saved = unitRepository.save(com.tqp.cms.entity.Unit.builder().name(normalizedName).build());
        return unitMapper.toResponse(saved);
    }


    @Override
    public Page<UnitResponse> getUnits(int page, int size, String name) {
        Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<com.tqp.cms.entity.Unit> result;
        if (name != null && !name.isBlank()) {
            result = unitRepository.findByActiveTrueAndNameContainingIgnoreCase(name, pageable);
        } else {
            result = unitRepository.findByActiveTrue(pageable);
        }
        return result.map(unitMapper::toResponse);
    }

    @Override
    @Transactional
    public UnitResponse updateUnit(UUID unitId, UnitUpdateRequest request) {
        var unit = unitRepository.findById(unitId)
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));

        if (request.getName() != null) {
            String normalizedName = request.getName().trim();
            if (unitRepository.existsByNameAndIdNot(normalizedName, unitId)) {
                throw new AppException(ErrorCode.UNIT_EXISTED);
            }
            unit.setName(normalizedName);
        }

        return unitMapper.toResponse(unitRepository.save(unit));
    }

    @Override
    @Transactional
    public void softDeleteUnit(UUID unitId) {
        var unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));
        unitRepository.delete(unit);
    }
}
