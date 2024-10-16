package com.respo.respo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.respo.respo.Entity.WalletEntity;

public interface WalletRepository extends JpaRepository<WalletEntity, Integer> {
    WalletEntity findByUser_UserId(int userId);
}
