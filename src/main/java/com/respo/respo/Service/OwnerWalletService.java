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

    public void addToOnlineEarnings(int userId, double rentPrice) {
        OwnerWalletEntity wallet = getWalletByUserId(userId);
    
        // Calculate 85% for the withdrawable balance
        double onlineAmount = rentPrice * 0.85;
        wallet.setOnlineEarning(wallet.getOnlineEarning() + onlineAmount);
    
        // Automatically deduct outstanding balance from online earnings
        deductCashFromOnlineEarnings(wallet);
    
        ownerWalletRepository.save(wallet);
    }

    public void addToCashEarnings(int userId, double rentPrice) {
        OwnerWalletEntity wallet = getWalletByUserId(userId);
    
        // Calculate 15% for the outstanding balance
        double outstandingAmount = rentPrice * 0.15;
        wallet.setCashEarning(wallet.getCashEarning() + outstandingAmount);
    
        // Automatically deduct outstanding balance from online earnings
        deductCashFromOnlineEarnings(wallet);
    
        ownerWalletRepository.save(wallet);
    }

    public void updateCashRefundable(int userId, double amount) {
        OwnerWalletEntity wallet = getWalletByUserId(userId);
        wallet.setCashRefundable(wallet.getCashRefundable() + amount);
        // Automatically handle deductions after updating cash refundable
        deductCashFromOnlineEarnings(wallet);
        ownerWalletRepository.save(wallet);
    }

    // Automatically deduct cash earnings from online earnings if cash earning has balance
    private void deductCashFromOnlineEarnings(OwnerWalletEntity wallet) {
        if (wallet.getCashEarning() > 0) { // Check if there's an outstanding balance
            double outstandingBalance = wallet.getCashEarning();
            double onlineEarning = wallet.getOnlineEarning();
    
            if (onlineEarning >= outstandingBalance) {
                // Deduct full outstanding balance from online earnings
                wallet.setOnlineEarning(onlineEarning - outstandingBalance);
                wallet.setCashEarning(0.0); // Clear the outstanding balance
            } else {
                // Partial deduction if online earnings are insufficient
                wallet.setCashEarning(outstandingBalance - onlineEarning);
                wallet.setOnlineEarning(0.0); // Clear online earnings
            }
        }
    }
    
    public boolean deductRefundAmount(int userId, double refundAmount) {
        OwnerWalletEntity wallet = ownerWalletRepository.findByUserUserId(userId);
    
        if (wallet != null) {
            // Deduct refundAmount from the online earnings (owner's wallet)
            double updatedBalance = wallet.getOnlineEarning() - refundAmount;
    
            // Allow negative balances
            wallet.setOnlineEarning(updatedBalance); // Update online earnings
    
            // Save the updated wallet after deduction
            ownerWalletRepository.save(wallet);
            return true;
        }
    
        // Return false if wallet doesn't exist
        return false;
    }
    
}
