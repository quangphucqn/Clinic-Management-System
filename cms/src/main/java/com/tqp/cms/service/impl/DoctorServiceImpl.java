package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.DoctorCreationRequest;
import com.tqp.cms.dto.request.DoctorSelfUpdateRequest;
import com.tqp.cms.dto.request.DoctorUpdateRequest;
import com.tqp.cms.dto.response.DoctorDetailResponse;
import com.tqp.cms.dto.response.DoctorResponse;
import com.tqp.cms.entity.Doctor;
import com.tqp.cms.entity.Users;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
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
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public DoctorDetailResponse createDoctor(DoctorCreationRequest request) {
        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new AppException(ErrorCode.DOCTOR_EXISTED);
        }
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        var specialty = specialtyRepository.findById(request.getSpecialtyId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));

        Users user = Users.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(UserRole.DOCTOR)
                .build();
        user = usersRepository.save(user);

        Doctor doctor = Doctor.builder()
                .userAccount(user)
                .specialty(specialty)
                .licenseNumber(request.getLicenseNumber())
                .roomNumber(request.getRoomNumber())
                .yearsOfExperience(request.getYearsOfExperience())
                .biography(request.getBiography())
                .build();

        return doctorMapper.toDetailResponse(doctorRepository.save(doctor));
    }

    @Override
    public Page<DoctorResponse> getDoctors(int page, int size, String keyword, UUID specialtyId) {
        Pageable pageable = PageRequest.of(page, size,Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Doctor> result;
        if (keyword != null && !keyword.isBlank() && specialtyId != null) {
            result = doctorRepository.searchActiveDoctorsBySpecialty(keyword, specialtyId, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            result = doctorRepository.searchActiveDoctors(keyword, pageable);
        } else if (specialtyId != null) {
            result = doctorRepository.findByActiveTrueAndSpecialtyId(specialtyId, pageable);
        } else {
            result = doctorRepository.findByActiveTrue(pageable);
        }
        return result.map(doctorMapper::toBasicResponse);
    }

    @Override
    public DoctorDetailResponse getDoctorById(UUID doctorId) {
        var doctor = doctorRepository.findById(doctorId)
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
        return doctorMapper.toDetailResponse(doctor);
    }

    @Override
    @Transactional
    public DoctorDetailResponse updateDoctor(UUID doctorId, DoctorUpdateRequest request) {
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

        return doctorMapper.toDetailResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public DoctorDetailResponse updateMyProfile(DoctorSelfUpdateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var doctor = doctorRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        if (request.getEmail() != null) {
            if (usersRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getLicenseNumber() != null) {
            if (doctorRepository.existsByLicenseNumberAndIdNot(request.getLicenseNumber(), doctor.getId())) {
                throw new AppException(ErrorCode.DOCTOR_EXISTED);
            }
            doctor.setLicenseNumber(request.getLicenseNumber());
        }
        if (request.getBiography() != null) {
            doctor.setBiography(request.getBiography());
        }

        usersRepository.save(user);
        return doctorMapper.toDetailResponse(doctorRepository.save(doctor));
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
