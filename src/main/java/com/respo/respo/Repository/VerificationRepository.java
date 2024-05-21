package com.respo.respo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.respo.respo.Entity.VerificationEntity;
import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationEntity, Integer> {
    Optional<VerificationEntity> findByUser_UserId(int userId);
}
