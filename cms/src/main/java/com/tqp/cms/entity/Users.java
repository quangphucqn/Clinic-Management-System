package com.tqp.cms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE users SET active = false WHERE id = ?")
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    String username;

    @NotBlank
    @Column(nullable = false, length = 255)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    UserRole role;

    @NotBlank
    @Column(nullable = false, length = 100)
    String fullName;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    String email;

    @Column(length = 15)
    String phoneNumber;

}
