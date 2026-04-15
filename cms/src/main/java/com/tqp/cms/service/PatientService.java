package com.tqp.cms.service;

import com.tqp.cms.dto.request.PatientSelfUpdateRequest;
import com.tqp.cms.dto.request.PatientRegistrationRequest;
import com.tqp.cms.dto.response.PatientSelfProfileResponse;
import com.tqp.cms.dto.response.PatientRegistrationResponse;

public interface PatientService {
    PatientRegistrationResponse registerPatient(PatientRegistrationRequest request);

    PatientSelfProfileResponse updateMyProfile(PatientSelfUpdateRequest request);
}
