package com.mariuszilinskas.vsp.authservice.repository;

import com.mariuszilinskas.vsp.authservice.model.HashedPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Hashed Password entities. Supports standard CRUD operations.
 *
 * @author Marius Zilinskas
 */
@Repository
public interface HashedPasswordRepository extends JpaRepository<HashedPassword, UUID> {

    Optional<HashedPassword> findByUserId(UUID userId);

}
