package com.tqp.cms.mapper;

import com.tqp.cms.dto.response.DoctorDetailResponse;
import com.tqp.cms.dto.response.DoctorResponse;
import com.tqp.cms.entity.Doctor;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {
    public DoctorResponse toBasicResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .userId(doctor.getUserAccount() != null ? doctor.getUserAccount().getId() : null)
                .fullName(doctor.getUserAccount() != null ? doctor.getUserAccount().getFullName() : null)
                .specialtyId(doctor.getSpecialty() != null ? doctor.getSpecialty().getId() : null)
                .specialtyName(doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : null)
                .licenseNumber(doctor.getLicenseNumber()!=null ? doctor.getLicenseNumber() : null)
                .roomNumber(doctor.getRoomNumber())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .build();
    }

    public DoctorDetailResponse toDetailResponse(Doctor doctor) {
        return DoctorDetailResponse.builder()
                .id(doctor.getId())
                .userId(doctor.getUserAccount() != null ? doctor.getUserAccount().getId() : null)
                .username(doctor.getUserAccount() != null ? doctor.getUserAccount().getUsername() : null)
                .fullName(doctor.getUserAccount() != null ? doctor.getUserAccount().getFullName() : null)
                .email(doctor.getUserAccount() != null ? doctor.getUserAccount().getEmail() : null)
                .phoneNumber(doctor.getUserAccount() != null ? doctor.getUserAccount().getPhoneNumber() : null)
                .specialtyId(doctor.getSpecialty() != null ? doctor.getSpecialty().getId() : null)
                .specialtyName(doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : null)
                .licenseNumber(doctor.getLicenseNumber())
                .roomNumber(doctor.getRoomNumber())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .biography(doctor.getBiography())
                .active(doctor.isActive())
                .createdAt(doctor.getCreatedAt())
                .updatedAt(doctor.getUpdatedAt())
                .build();
    }
}
