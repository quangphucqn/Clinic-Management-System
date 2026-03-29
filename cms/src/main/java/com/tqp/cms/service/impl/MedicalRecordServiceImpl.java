package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.MedicalRecordRequest;
import com.tqp.cms.dto.response.MedicalRecordDetailResponse;
import com.tqp.cms.dto.response.MedicalRecordResponse;
import com.tqp.cms.entity.Appointment;
import com.tqp.cms.entity.AppointmentStatus;
import com.tqp.cms.entity.MedicalRecord;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.mapper.MedicalRecordDetailMapper;
import com.tqp.cms.mapper.MedicalRecordMapper;
import com.tqp.cms.repository.AppointmentRepository;
import com.tqp.cms.repository.MedicalRecordRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.MedicalRecordService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MedicalRecordServiceImpl implements MedicalRecordService {

    AppointmentRepository appointmentRepository;
    MedicalRecordRepository medicalRecordRepository;
    MedicalRecordMapper medicalRecordMapper;
    UsersRepository usersRepository;
    MedicalRecordDetailMapper medicalRecordDetailMapper;


    @Override
    public MedicalRecordResponse createMedicalRecord (MedicalRecordRequest request) {

        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        var user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var doctor = user.getDoctorProfile();


        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (medicalRecordRepository.existsByAppointmentId(appointment.getId())) {
            throw new AppException(ErrorCode.MEDICAL_RECORD_EXISTED);
        }

        MedicalRecord record = medicalRecordMapper.toEntity(
                request,
                appointment,
                appointment.getPatient(),
                doctor
        );

        appointment.setStatus(AppointmentStatus.COMPLETED);

        return medicalRecordMapper.toResponse(medicalRecordRepository.save(record));
    }

    @Override
    public MedicalRecordDetailResponse getMedicalRecordById(UUID id) {

        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        var user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var doctor = user.getDoctorProfile();

        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEDICAL_RECORD_NOT_FOUND));

        if (!record.getDoctor().getId().equals(doctor.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }


        return medicalRecordDetailMapper.toDetailResponse(record);
    }
}
