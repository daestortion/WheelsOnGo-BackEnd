package com.respo.respo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.respo.respo.Entity.OwnerWalletEntity;

public interface OwnerWalletRepository extends JpaRepository<OwnerWalletEntity, Integer> {
    OwnerWalletEntity findByUserUserId(int userId);
}
