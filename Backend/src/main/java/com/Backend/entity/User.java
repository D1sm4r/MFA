package com.Backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    private String email;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "mfa_enabled")
    private boolean mfaEnabled;
}
