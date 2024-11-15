package com.respo.respo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.respo.respo.Entity.OwnerWalletEntity;
import com.respo.respo.Repository.OwnerWalletRepository;

@Service
public class OwnerWalletService {

    @Autowired
    private OwnerWalletRepository ownerWalletRepository;

    public OwnerWalletEntity getWalletByUserId(int userId) {
        return ownerWalletRepository.findByUserUserId(userId);
    }

    public OwnerWalletEntity createOrUpdateWallet(OwnerWalletEntity wallet) {
        return ownerWalletRepository.save(wallet);
    }

    // Add 85% of the given amount to online earnings
    public void addToOnlineEarnings(int userId, float amount) {
        OwnerWalletEntity wallet = getWalletByUserId(userId);
        float addAmount = amount * 0.85f;
        wallet.setOnlineEarning(wallet.getOnlineEarning() + addAmount);
        ownerWalletRepository.save(wallet);
    }

    // Add 85% of the given amount to cash earnings
    public void addToCashEarnings(int userId, float amount) {
        OwnerWalletEntity wallet = getWalletByUserId(userId);
        float addAmount = amount * 0.85f;
        wallet.setCashEarning(wallet.getCashEarning() + addAmount);
        ownerWalletRepository.save(wallet);
    }
    
    public void updateCashRefundable(int userId, float amount) {
        OwnerWalletEntity wallet = getWalletByUserId(userId);
        wallet.setCashRefundable(wallet.getCashRefundable() + amount);
        ownerWalletRepository.save(wallet);
    }
}
