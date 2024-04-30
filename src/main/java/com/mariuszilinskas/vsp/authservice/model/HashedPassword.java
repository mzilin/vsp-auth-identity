package com.mariuszilinskas.vsp.authservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * This entity describes Hashed User Passwords within the platform.
 *
 * @author Marius Zilinskas
 */
@Entity
@Getter
@Setter
@Table(name = "hashed_passwords")
public class HashedPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "incorrect_entries")
    private int incorrectEntries = 0;

    @Column(name = "last_updated", nullable = false)
    private ZonedDateTime lastUpdated = ZonedDateTime.now();

}
