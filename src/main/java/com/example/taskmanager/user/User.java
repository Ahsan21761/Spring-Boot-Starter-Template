package com.example.taskmanager.user;

import com.example.taskmanager.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Application user. This is a pure persistence entity; the Spring Security adapter lives in
 * {@code AppUserDetails} so that the domain model stays free of framework concerns.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
public class User extends BaseEntity {

    @Column(name = "email", nullable = false)
    private String email;

    /** BCrypt-hashed password. Never stores plaintext. */
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 32)
    private Role role = Role.USER;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;
}
