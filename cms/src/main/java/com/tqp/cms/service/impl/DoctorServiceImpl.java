package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.DoctorCreationRequest;
import com.tqp.cms.dto.request.DoctorUpdateRequest;
import com.tqp.cms.dto.response.DoctorResponse;
import com.tqp.cms.entity.Doctor;
import com.tqp.cms.entity.UserRole;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.DoctorMapper;
import com.tqp.cms.repository.DoctorRepository;
import com.tqp.cms.repository.SpecialtyRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.DoctorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DoctorServiceImpl implements DoctorService {
    DoctorRepository doctorRepository;
    UsersRepository usersRepository;
    SpecialtyRepository specialtyRepository;
    DoctorMapper doctorMapper;

    @Override
    @Transactional
    public DoctorResponse createDoctor(DoctorCreationRequest request) {
        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new AppException(ErrorCode.DOCTOR_EXISTED);
        }
        if (doctorRepository.existsByUserAccountId(request.getUserId())) {
            throw new AppException(ErrorCode.DOCTOR_EXISTED);
        }

        var user = usersRepository.findById(request.getUserId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var specialty = specialtyRepository.findById(request.getSpecialtyId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));

        user.setRole(UserRole.DOCTOR);
        usersRepository.save(user);

        Doctor doctor = Doctor.builder()
                .userAccount(user)
                .specialty(specialty)
                .licenseNumber(request.getLicenseNumber())
                .roomNumber(request.getRoomNumber())
                .yearsOfExperience(request.getYearsOfExperience())
                .biography(request.getBiography())
                .build();

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    public Page<DoctorResponse> getDoctors(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Doctor> result;
        if (keyword != null && !keyword.isBlank()) {
            result = doctorRepository.searchActiveDoctors(keyword, pageable);
        } else {
            result = doctorRepository.findByActiveTrue(pageable);
        }
        return result.map(doctorMapper::toResponse);
    }

    @Override
    public DoctorResponse getDoctorById(UUID doctorId) {
        var doctor = doctorRepository.findById(doctorId)
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
        return doctorMapper.toResponse(doctor);
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctor(UUID doctorId, DoctorUpdateRequest request) {
        var doctor = doctorRepository.findById(doctorId)
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        if (request.getSpecialtyId() != null) {
            var specialty = specialtyRepository.findById(request.getSpecialtyId())
                    .filter(item -> item.isActive())
                    .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));
            doctor.setSpecialty(specialty);
        }
        if (request.getLicenseNumber() != null) {
            if (doctorRepository.existsByLicenseNumberAndIdNot(request.getLicenseNumber(), doctorId)) {
                throw new AppException(ErrorCode.DOCTOR_EXISTED);
            }
            doctor.setLicenseNumber(request.getLicenseNumber());
        }
        if (request.getRoomNumber() != null) {
            doctor.setRoomNumber(request.getRoomNumber());
        }
        if (request.getYearsOfExperience() != null) {
            doctor.setYearsOfExperience(request.getYearsOfExperience());
        }
        if (request.getBiography() != null) {
            doctor.setBiography(request.getBiography());
        }

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public void softDeleteDoctor(UUID doctorId) {
        UUID userId = doctorRepository.findActiveUserIdByDoctorId(doctorId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
        usersRepository.deactivateById(userId);
        doctorRepository.softDeleteById(doctorId);
    }
}
