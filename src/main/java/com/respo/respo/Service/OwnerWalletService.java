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
        OwnerWalletEntity wallet = ownerWalletRepository.findByUserUserId(userId);
        if (wallet != null) {
            // Automatically handle deductions whenever the wallet is accessed
            deductCashFromOnlineEarnings(wallet);
        }
        return wallet;
    }

    public OwnerWalletEntity createOrUpdateWallet(OwnerWalletEntity wallet) {
        // Automatically handle deductions on every update
        deductCashFromOnlineEarnings(wallet);
        return ownerWalletRepository.save(wallet);
    }

    public void addToOnlineEarnings(int userId, float amount) {
        OwnerWalletEntity wallet = getWalletByUserId(userId);
        float addAmount = amount * 0.85f;
        wallet.setOnlineEarning(wallet.getOnlineEarning() + addAmount);
        // Automatically handle deductions after adding online earnings
        deductCashFromOnlineEarnings(wallet);
        ownerWalletRepository.save(wallet);
    }

    public void addToCashEarnings(int userId, float amount) {
        OwnerWalletEntity wallet = getWalletByUserId(userId);
        float addAmount = amount * 0.15f;
        wallet.setCashEarning(wallet.getCashEarning() + addAmount);
        // Automatically handle deductions after adding cash earnings
        deductCashFromOnlineEarnings(wallet);
        ownerWalletRepository.save(wallet);
    }

    public void updateCashRefundable(int userId, float amount) {
        OwnerWalletEntity wallet = getWalletByUserId(userId);
        wallet.setCashRefundable(wallet.getCashRefundable() + amount);
        // Automatically handle deductions after updating cash refundable
        deductCashFromOnlineEarnings(wallet);
        ownerWalletRepository.save(wallet);
    }

    // Automatically deduct cash earnings from online earnings if cash earning has balance
    private void deductCashFromOnlineEarnings(OwnerWalletEntity wallet) {
        if (wallet.getCashEarning() > 0) {
            float cashBalance = wallet.getCashEarning();
            wallet.setOnlineEarning(wallet.getOnlineEarning() - cashBalance);
            wallet.setCashEarning(0.0f); // Reset cash earning after transferring
        }
    }
}
