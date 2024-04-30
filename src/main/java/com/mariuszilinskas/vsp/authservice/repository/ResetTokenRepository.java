package com.mariuszilinskas.vsp.authservice.repository;

import com.mariuszilinskas.vsp.authservice.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Reset Token entities. Supports standard CRUD operations.
 *
 * @author Marius Zilinskas
 */
@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken, UUID> {

    Optional<ResetToken> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    void deleteAllByExpiryDateBefore(Instant expiryDate);

}
