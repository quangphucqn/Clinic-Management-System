package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.MedicalHistoryRequest;
import com.tqp.cms.dto.response.MedicalHistoryResponse;
import com.tqp.cms.dto.response.PageResponse;
import com.tqp.cms.entity.Doctor;
import com.tqp.cms.entity.MedicalRecord;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.MedicalHistoryMapper;
import com.tqp.cms.repository.MedicalRecordRepository;
import com.tqp.cms.service.MedicalHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    MedicalRecordRepository medicalRecordRepository;
    MedicalHistoryMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MedicalHistoryResponse> getPatientHistory(UUID patientId, MedicalHistoryRequest request) {

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by("visitedAt").descending()
        );

        Page<MedicalRecord> pageData = medicalRecordRepository.findByPatientId(patientId, pageable);

        return PageResponse.<MedicalHistoryResponse>builder()
                .content(
                        pageData.getContent()
                                .stream()
                                .map(mapper::toSummary)
                                .toList()
                )
                .page(pageData.getNumber())
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .build();
    }
}