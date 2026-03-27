package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.UserCreationRequest;
import com.tqp.cms.dto.request.UserUpdateRequest;
import com.tqp.cms.dto.response.CurrentUserProfileResponse;
import com.tqp.cms.dto.response.DoctorProfileResponse;
import com.tqp.cms.dto.response.PatientProfileResponse;
import com.tqp.cms.dto.response.UserResponse;
import com.tqp.cms.entity.UserRole;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.UserMapper;
import com.tqp.cms.repository.DoctorRepository;
import com.tqp.cms.repository.PatientRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepository usersRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toResponse(usersRepository.save(user));
    }

    @Override
    public Page<UserResponse> getUsers(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.tqp.cms.entity.Users> result;
        if (username != null && !username.isBlank()) {
            result = usersRepository.findByActiveTrueAndUsernameContainingIgnoreCase(username, pageable);
        } else {
            result = usersRepository.findByActiveTrue(pageable);
        }
        return result.map(userMapper::toResponse);
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        var user = usersRepository.findById(userId)
                .filter(u -> u.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    //Current user
    @Override
    public CurrentUserProfileResponse getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        DoctorProfileResponse doctorProfile = null;
        PatientProfileResponse patientProfile = null;

        if (user.getRole() == UserRole.DOCTOR) {
            var doctor = doctorRepository.findByUserAccountId(user.getId())
                    .filter(item -> item.isActive())
                    .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
            doctorProfile = DoctorProfileResponse.builder()
                    .doctorId(doctor.getId())
                    .specialtyId(doctor.getSpecialty().getId())
                    .specialtyName(doctor.getSpecialty().getName())
                    .licenseNumber(doctor.getLicenseNumber())
                    .roomNumber(doctor.getRoomNumber())
                    .yearsOfExperience(doctor.getYearsOfExperience())
                    .biography(doctor.getBiography())
                    .build();
        } else if (user.getRole() == UserRole.PATIENT) {
            var patient = patientRepository.findByUserAccountId(user.getId())
                    .filter(item -> item.isActive())
                    .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));
            patientProfile = PatientProfileResponse.builder()
                    .patientId(patient.getId())
                    .gender(patient.getGender())
                    .dateOfBirth(patient.getDateOfBirth())
                    .address(patient.getAddress())
                    .emergencyContactName(patient.getEmergencyContactName())
                    .emergencyContactPhone(patient.getEmergencyContactPhone())
                    .build();
        }

        return CurrentUserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .doctorProfile(doctorProfile)
                .patientProfile(patientProfile)
                .build();
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID userId, UserUpdateRequest request) {
        var user = usersRepository.findById(userId)
                .filter(u -> u.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getUsername() != null) {
            if (usersRepository.existsByUsernameAndIdNot(request.getUsername(), userId)) {
                throw new AppException(ErrorCode.USER_EXISTED);
            }
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            if (usersRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        return userMapper.toResponse(usersRepository.save(user));
    }

    @Override
    @Transactional
    public void softDeleteUser(UUID userId) {
        var user = usersRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        usersRepository.delete(user);
    }
}
