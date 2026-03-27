package com.tqp.cms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "doctors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE doctors SET active = false WHERE id = ?")
public class Doctor extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    Users userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    Specialty specialty;

    @Column(nullable = false, unique = true, length = 50)
    String licenseNumber;

    @Column(length = 20)
    String roomNumber;

    @Column(nullable = false)
    Integer yearsOfExperience;

    @Column(columnDefinition = "TEXT")
    String biography;

    @OneToMany(mappedBy = "doctor")
    List<Appointment> appointments;

    @OneToMany(mappedBy = "doctor")
    List<MedicalRecord> medicalRecords;

    @OneToMany(mappedBy = "doctor")
    List<DoctorReview> doctorReviews;
}
