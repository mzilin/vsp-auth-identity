package com.mariuszilinskas.vsp.authservice.repository;

import com.mariuszilinskas.vsp.authservice.model.Passcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Passcode entities. Supports standard CRUD operations.
 *
 * @author Marius Zilinskas
 */
@Repository
public interface PasscodeRepository extends JpaRepository<Passcode, UUID> {

    Optional<Passcode> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

}
