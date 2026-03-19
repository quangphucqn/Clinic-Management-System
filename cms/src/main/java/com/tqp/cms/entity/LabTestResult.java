package com.tqp.cms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "lab_test_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE lab_test_results SET active = false WHERE id = ?")
public class LabTestResult extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_test_order_id", nullable = false, unique = true)
    LabTestOrder labTestOrder;

    @Column(nullable = false, columnDefinition = "TEXT")
    String resultValue;

    @Column(length = 100)
    String normalRange;

    @Column(length = 255)
    String attachmentUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime reportedAt;
}
