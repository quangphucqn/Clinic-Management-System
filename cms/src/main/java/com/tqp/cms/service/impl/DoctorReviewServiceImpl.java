package com.tqp.cms.service.impl;

import com.tqp.cms.dto.request.DoctorReviewCreationRequest;
import com.tqp.cms.dto.response.DoctorReviewResponse;
import com.tqp.cms.dto.response.PageResponse;
import com.tqp.cms.dto.response.PatientDoctorReviewResponse;
import com.tqp.cms.entity.AppointmentStatus;
import com.tqp.cms.entity.DoctorReview;
import com.tqp.cms.exception.AppException;
import com.tqp.cms.exception.ErrorCode;
import com.tqp.cms.repository.AppointmentRepository;
import com.tqp.cms.repository.DoctorReviewRepository;
import com.tqp.cms.repository.PatientRepository;
import com.tqp.cms.repository.UsersRepository;
import com.tqp.cms.service.DoctorReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DoctorReviewServiceImpl implements DoctorReviewService {
    DoctorReviewRepository doctorReviewRepository;
    AppointmentRepository appointmentRepository;
    UsersRepository usersRepository;
    PatientRepository patientRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('PATIENT')")
    public DoctorReviewResponse createReview(DoctorReviewCreationRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var patient = patientRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        var appointment = appointmentRepository.findById(request.getAppointmentId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new AppException(ErrorCode.APPOINTMENT_STATUS_INVALID);
        }

        if (doctorReviewRepository.existsByAppointmentIdAndPatientIdAndActiveTrue(appointment.getId(), patient.getId())) {
            throw new AppException(ErrorCode.REVIEW_EXISTED);
        }

        if (doctorReviewRepository.existsByDoctorIdAndPatientIdAndActiveTrue(appointment.getDoctor().getId(), patient.getId())) {
            throw new AppException(ErrorCode.REVIEW_EXISTED);
        }

        DoctorReview review = DoctorReview.builder()
                .appointment(appointment)
                .patient(patient)
                .doctor(appointment.getDoctor())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        DoctorReview savedReview = doctorReviewRepository.save(review);

        return DoctorReviewResponse.builder()
                .reviewId(savedReview.getId())
                .appointmentId(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .rating(savedReview.getRating())
                .comment(savedReview.getComment())
                .reviewedAt(savedReview.getReviewedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('PATIENT')")
    public PageResponse<PatientDoctorReviewResponse> getMyReviews(int page, int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = usersRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var patient = patientRepository.findByUserAccountId(user.getId())
                .filter(item -> item.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reviewedAt"));
        var reviews = doctorReviewRepository.findByPatientIdAndActiveTrueOrderByReviewedAtDesc(patient.getId(), pageable);

        return PageResponse.<PatientDoctorReviewResponse>builder()
                .content(reviews.getContent().stream().map(this::toPatientReviewResponse).toList())
                .page(reviews.getNumber())
                .size(reviews.getSize())
                .totalElements(reviews.getTotalElements())
                .totalPages(reviews.getTotalPages())
                .build();
    }

    private PatientDoctorReviewResponse toPatientReviewResponse(DoctorReview review) {
        return PatientDoctorReviewResponse.builder()
                .reviewId(review.getId())
                .appointmentId(review.getAppointment().getId())
                .doctorId(review.getDoctor().getId())
                .doctorName(review.getDoctor().getUserAccount().getFullName())
                .appointmentDate(review.getAppointment().getAppointmentDate())
                .timeSlot(
                        review.getAppointment().getTimeSlotConfig().getStartTime()
                                + " - "
                                + review.getAppointment().getTimeSlotConfig().getEndTime()
                )
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewedAt(review.getReviewedAt())
                .build();
    }
}
