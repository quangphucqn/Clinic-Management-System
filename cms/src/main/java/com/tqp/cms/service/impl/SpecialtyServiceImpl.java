package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.SpecialtyCreationRequest;
import com.tqp.cms.dto.request.SpecialtyUpdateRequest;
import com.tqp.cms.dto.response.SpecialtyResponse;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.SpecialtyMapper;
import com.tqp.cms.repository.SpecialtyRepository;
import com.tqp.cms.service.SpecialtyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpecialtyServiceImpl implements SpecialtyService {
    SpecialtyRepository specialtyRepository;
    SpecialtyMapper specialtyMapper;

    @Override
    @Transactional
    public SpecialtyResponse createSpecialty(SpecialtyCreationRequest request) {
        if (specialtyRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.SPECIALTY_EXISTED);
        }
        var saved = specialtyRepository.save(specialtyMapper.toEntity(request));
        return specialtyMapper.toResponse(saved);
    }

    @Override
    public SpecialtyResponse getSpecialtyById(UUID specialtyId) {
        var specialty = specialtyRepository.findById(specialtyId)
                .filter(s -> s.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));
        return specialtyMapper.toResponse(specialty);
    }

    @Override
    public Page<SpecialtyResponse> getSpecialties(int page, int size, String name) {
        Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<com.tqp.cms.entity.Specialty> result;
        if (name != null && !name.isBlank()) {
            result = specialtyRepository.findByActiveTrueAndNameContainingIgnoreCase(name, pageable);
        } else {
            result = specialtyRepository.findByActiveTrue(pageable);
        }
        return result.map(specialtyMapper::toResponse);
    }

    @Override
    @Transactional
    public SpecialtyResponse updateSpecialty(UUID specialtyId, SpecialtyUpdateRequest request) {
        var specialty = specialtyRepository.findById(specialtyId)
                .filter(s -> s.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));

        if (request.getName() != null) {
            if (specialtyRepository.existsByNameAndIdNot(request.getName(), specialtyId)) {
                throw new AppException(ErrorCode.SPECIALTY_EXISTED);
            }
            specialty.setName(request.getName());
        }
        if (request.getDescription() != null) {
            specialty.setDescription(request.getDescription());
        }

        return specialtyMapper.toResponse(specialtyRepository.save(specialty));
    }

    @Override
    @Transactional
    public void softDeleteSpecialty(UUID specialtyId) {
        var specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));
        specialtyRepository.delete(specialty);
    }
}
