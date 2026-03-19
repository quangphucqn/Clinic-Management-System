package com.tqp.cms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "patients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE patients SET active = false WHERE id = ?")
public class Patient extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    Users userAccount;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    Gender gender;

    LocalDate dateOfBirth;

    @Column(length = 255)
    String address;

    @Column(length = 100)
    String emergencyContactName;

    @Column(length = 15)
    String emergencyContactPhone;

    @OneToMany(mappedBy = "patient")
    List<Appointment> appointments;

    @OneToMany(mappedBy = "patient")
    List<MedicalRecord> medicalRecords;

    @OneToMany(mappedBy = "patient")
    List<PaymentTransaction> paymentTransactions;

    @OneToMany(mappedBy = "patient")
    List<DoctorReview> doctorReviews;
}
