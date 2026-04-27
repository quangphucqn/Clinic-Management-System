package com.tqp.cms.service;

import com.tqp.cms.dto.response.PageResponse;
import com.tqp.cms.dto.response.PatientAppoinmentDoctorResponse;
import org.springframework.data.domain.Page;

public interface PatientDoctorService {
    PageResponse<PatientAppoinmentDoctorResponse> searchPatients(
            String name,
            int page,
            int size
    );
}
