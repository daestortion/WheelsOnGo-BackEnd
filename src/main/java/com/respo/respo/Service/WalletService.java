package com.respo.respo.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.OwnerWalletEntity;
import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Repository.OwnerWalletRepository;
import com.respo.respo.Repository.RequestFormRepository;
import com.respo.respo.Repository.UserRepository;
import com.respo.respo.Repository.WalletRepository;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private OwnerWalletRepository ownerWalletRepository;
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

    public void addToBalance(int userId, float amount) {
        WalletEntity wallet = walletRepository.findByUser_UserId(userId);
        if (wallet != null) {
            wallet.setBalance(wallet.getBalance() + amount);
            walletRepository.save(wallet);
        } else {
            throw new NoSuchElementException("Wallet not found for userId: " + userId);
        }
    }

    public void processRefund(int userId, float refundAmount) {
    WalletEntity wallet = walletRepository.findByUser_UserId(userId);
    if (wallet == null) {
        throw new NoSuchElementException("Wallet not found for user ID: " + userId);
    }
    wallet.setBalance(wallet.getBalance() + refundAmount);
    walletRepository.save(wallet);
    }

    public void processTerminationFee(int ownerId, float terminationFee) {
        OwnerWalletEntity wallet = ownerWalletRepository.findByUserUserId(ownerId);
        if (wallet == null) {
            throw new NoSuchElementException("Owner Wallet not found for user ID: " + ownerId);
        }
        wallet.setCashEarning(wallet.getCashEarning() + terminationFee);
        ownerWalletRepository.save(wallet);
    }

    public double processRefund(int orderId) {
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoSuchElementException("Order not found"));

        LocalDate currentDate = LocalDate.now();
        long daysBeforeBooking = ChronoUnit.DAYS.between(currentDate, order.getStartDate());
        double totalPrice = order.getTotalPrice();
        double refundAmount = 0.0;

        if (daysBeforeBooking >= 3) {
            refundAmount = totalPrice * 0.9; // 90% refund
        } else if (daysBeforeBooking >= 1) {
            refundAmount = totalPrice * 0.8; // 80% refund
        } else if (daysBeforeBooking == 0) {
            refundAmount = 0.0; // No refund
        }

        WalletEntity wallet = walletRepository.findByUser_UserId(order.getUser().getUserId());
        if (wallet == null) {
            throw new NoSuchElementException("Wallet not found for user ID: " + order.getUser().getUserId());
        }

        wallet.setBalance(wallet.getBalance() + refundAmount);
        walletRepository.save(wallet);

        return refundAmount;
    }
}