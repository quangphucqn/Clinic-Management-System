package com.tqp.cms.service.impl;


import com.tqp.cms.dto.response.PageResponse;
import com.tqp.cms.dto.response.PatientAppoinmentDoctorResponse;
import com.tqp.cms.entity.Patient;
import com.tqp.cms.mapper.PatientMapper;
import com.tqp.cms.repository.PatientRepository;
import com.tqp.cms.service.PatientDoctorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PatientDoctorServiceImpl implements PatientDoctorService {

    PatientRepository patientRepository;
    PatientMapper mapper;

    @Override
    public PageResponse<PatientAppoinmentDoctorResponse> searchPatients(
            String name,
            int page,
            int size
    ) {
        if (name == null) name = "";

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("userAccount.fullName").ascending()
        );

        Page<Patient> patientPage =
                patientRepository.findByUserAccount_FullNameContainingIgnoreCase(name, pageable);

        return PageResponse.<PatientAppoinmentDoctorResponse>builder()
                .content(
                        patientPage.getContent()
                                .stream()
                                .map(mapper::toSearchResponse)
                                .toList()
                )
                .page(patientPage.getNumber())
                .size(patientPage.getSize())
                .totalElements(patientPage.getTotalElements())
                .totalPages(patientPage.getTotalPages())
                .build();
    }
}
