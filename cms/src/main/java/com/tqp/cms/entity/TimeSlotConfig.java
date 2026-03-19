package com.tqp.cms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalTime;
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
@Table(name = "time_slot_configs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE time_slot_configs SET active = false WHERE id = ?")
public class TimeSlotConfig extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false, unique = true, length = 30)
    String slotCode;

    @Column(nullable = false)
    LocalTime startTime;

    @Column(nullable = false)
    LocalTime endTime;

    @Column(nullable = false)
    Integer maxPatientsPerSlot;

    @Column(nullable = false)
    boolean enabled = true;

    @OneToMany(mappedBy = "timeSlotConfig")
    List<Appointment> appointments;
}
