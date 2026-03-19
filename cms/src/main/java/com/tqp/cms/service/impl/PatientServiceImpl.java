package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.PatientRegistrationRequest;
import com.tqp.cms.dto.response.PatientRegistrationResponse;
import com.tqp.cms.entity.Patient;
import com.tqp.cms.entity.UserRole;
import com.tqp.cms.entity.Users;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.repository.PatientRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.PatientService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PatientServiceImpl implements PatientService {
    UsersRepository usersRepository;
    PatientRepository patientRepository;
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public PatientRegistrationResponse registerPatient(PatientRegistrationRequest request) {
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        Users user = Users.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.PATIENT)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();
        Users savedUser = usersRepository.save(user);

        Patient patient = Patient.builder()
                .userAccount(savedUser)
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .build();
        Patient savedPatient = patientRepository.save(patient);

        return PatientRegistrationResponse.builder()
                .userId(savedUser.getId())
                .patientId(savedPatient.getId())
                .username(savedUser.getUsername())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }
}
