package com.respo.respo.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.RequestFormEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Repository.RequestFormRepository;
import com.respo.respo.Repository.UserRepository;
import com.respo.respo.Repository.WalletRepository;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RequestFormRepository requestFormRepository;

    public List<WalletEntity> getAllWallets() {
        return walletRepository.findAll();
    }

    public WalletEntity getWalletById(int id) {
        return walletRepository.findById(id).orElse(null);
    }

    public WalletEntity createWallet(WalletEntity walletEntity) {
        return walletRepository.save(walletEntity);
    }
    
    public WalletEntity addFundsToWallet(int userId, double amount) {
        WalletEntity walletEntity = walletRepository.findByUser_UserId(userId);
        if (walletEntity != null) {
            double updatedBalance = walletEntity.getBalance() + amount;
            walletEntity.setBalance(updatedBalance);
            return walletRepository.save(walletEntity); // Save updated wallet
        }
        return null; // If wallet doesn't exist for userId
    }
    // Fetch wallet by userId
    public WalletEntity getWalletByUserId(int userId) {
        return walletRepository.findByUser_UserId(userId);
    }

    // Logic for calculating refund amount and termination fee
    // Logic for fetching refund details (refundAmount and terminationFee)
    public RefundDetails getRefundDetails(int userId) {
        WalletEntity walletEntity = getWalletByUserId(userId);
        
        if (walletEntity == null) {
            return null; // Return null if the wallet doesn't exist for the given userId
        }

        // Assuming the refundAmount is the current balance in the wallet
        double refundAmount = walletEntity.getBalance();
        
        // Assume termination fee is fixed, or calculate as needed
        double terminationFee = 100.00; // Adjust based on your business logic

        return new RefundDetails(refundAmount, terminationFee);
    }

    // RefundDetails DTO class
    public static class RefundDetails {
        private double refundAmount;
        private double terminationFee;

        public RefundDetails(double refundAmount, double terminationFee) {
            this.refundAmount = refundAmount;
            this.terminationFee = terminationFee;
        }

        public double getRefundAmount() {
            return refundAmount;
        }

        public double getTerminationFee() {
            return terminationFee;
        }
    }

}