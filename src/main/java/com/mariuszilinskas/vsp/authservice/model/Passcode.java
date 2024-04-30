package com.mariuszilinskas.vsp.authservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * This entity describes Passcodes used for user account verification.
 *
 * @author Marius Zilinskas
 */
@Entity
@Getter
@Setter
@Table(name = "passcodes")
public class Passcode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id", nullable = false)
    private UUID userId;

    @JsonIgnore
    @Column(nullable = false)
    private String passcode;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

}
