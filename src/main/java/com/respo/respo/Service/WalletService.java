package com.respo.respo.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Repository.OrderRepository;
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
    public List<WalletEntity> getAllWallets() {
        return walletRepository.findAll();
    }
    public WalletEntity getWalletById(int id) {
        return walletRepository.findById(id).orElse(null);
    }
    public WalletEntity createWallet(WalletEntity walletEntity) {
        return walletRepository.save(walletEntity);
    }

    // Fetch wallet by user ID
    public WalletEntity getWalletByUserId(int userId) {
        System.out.println("Fetching wallet for user ID: " + userId);
        WalletEntity wallet = walletRepository.findByUser_UserId(userId);
        if (wallet == null || wallet.getUser() == null) {
            System.out.println("Wallet not found or wallet is not associated with a user");
            throw new RuntimeException("Wallet not found or wallet is not associated with a user");
        }
        System.out.println("Wallet found for user ID: " + userId);
        return wallet;
    }

    public WalletEntity updateWallet(WalletEntity walletEntity) {
        // Check if wallet is associated with a user before updating
        if (walletEntity.getUser() == null) {
            System.out.println("Error: User is not associated with the wallet.");
            throw new RuntimeException("User must be associated with the wallet.");
        }
    
        // Log the user details for debugging
        System.out.println("Updating wallet for user: " + walletEntity.getUser().getUserId());
    
        // Proceed with saving the updated wallet
        WalletEntity updatedWallet = walletRepository.save(walletEntity);
        System.out.println("Wallet updated successfully for user: " + walletEntity.getUser().getUserId());
        
        return updatedWallet;
    }
    
    
    public void deleteWallet(int id) {
        walletRepository.deleteById(id);
    }
    // Method to get paid orders for a specific user
    public List<OrderEntity> getPaidOrdersForUser(int userId) {
        return orderRepository.findAllByUser_UserIdAndIsPaid(userId, true);
    }
    public List<OrderEntity> getOrdersForOwnedCars(int userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null; // User not found
        }
        // Get all orders for the cars owned by the user
        return user.getCars().stream()
                .flatMap(car -> car.getOrders().stream())
                .collect(Collectors.toList());
    }

    @Transactional
    public float getCredit(int userId) {
        WalletEntity wallet = walletRepository.findByUser_UserId(userId);
        if (wallet == null) {
            throw new RuntimeException("Wallet not found for user ID: " + userId);
        }
    
        // Recalculate credit if orders exist, but prioritize manually adjusted balance
        List<OrderEntity> carOrders = getOrdersForOwnedCars(userId);
        if (carOrders == null || carOrders.isEmpty()) {
            return wallet.getCredit();  // Return the manually adjusted balance if no orders
        }
    
        // Calculate the credit based on orders
        float recalculatedCredit = (float) carOrders.stream()
            .filter(order -> ("online".equalsIgnoreCase(order.getPaymentOption()) || "PayPal".equalsIgnoreCase(order.getPaymentOption())) && !order.isTerminated())
            .mapToDouble(OrderEntity::getTotalPrice)
            .sum();
    
        // Only update the credit if recalculated credit is higher than the current credit
        if (wallet.getCredit() < recalculatedCredit) {
            wallet.setCredit(recalculatedCredit);
            walletRepository.save(wallet);  // Save updated balance
        }
    
        return wallet.getCredit();  // Return the final balance
    }
    

    @Transactional
    public float getDebit(int userId) {
        List<OrderEntity> carOrders = getOrdersForOwnedCars(userId);
        if (carOrders == null || carOrders.isEmpty()) {
            System.out.println("No orders found for user ID: " + userId);
            return 0;
        }

        // Calculate total debit (cash payments)
        float debit = (float) carOrders.stream()
                .filter(order -> "cash".equalsIgnoreCase(order.getPaymentOption()) && !order.isTerminated())
                .mapToDouble(OrderEntity::getTotalPrice)
                .sum();
        
        System.out.println("Debit calculated for user ID: " + userId + " = " + debit);

        // Save the recalculated debit to the wallet entity
        WalletEntity wallet = walletRepository.findByUser_UserId(userId);
        if (wallet == null) {
            System.out.println("Wallet not found for user ID: " + userId);
            throw new RuntimeException("Wallet not found for user ID: " + userId);
        }

        wallet.setDebit(debit);
        walletRepository.save(wallet);
        System.out.println("Debit updated in the database for user ID: " + userId);

        return debit;
    }

    @Transactional
    public float getRefundable(int userId) {
        List<OrderEntity> carOrders = getOrdersForOwnedCars(userId);
        if (carOrders == null || carOrders.isEmpty()) {
            return 0;
        }
        // Calculate refundable amount for terminated orders
        return (float) carOrders.stream()
                .filter(OrderEntity::isTerminated)
                .mapToDouble(order -> {
                    long daysBeforeStart = ChronoUnit.DAYS.between(order.getTerminationDate(), order.getStartDate());
                    if (daysBeforeStart >= 3) {
                        return order.getTotalPrice(); // 100% refund if 3 or more days before start date
                    } else if (daysBeforeStart > 0 && daysBeforeStart < 3) {
                        return order.getTotalPrice() * 0.2; // 20% refund if less than 3 days before start date
                    } else {
                        return 0; // No refund if on the start date
                    }
                })
                .sum();
    }

    @Transactional
    public void updateWalletBalances(int userId) {
        WalletEntity wallet = walletRepository.findByUser_UserId(userId);
        if (wallet == null) {
            throw new RuntimeException("Wallet not found for user ID: " + userId);
        }
    
        // Ensure recalculated credit respects the most recent manually updated balance
        float recalculatedCredit = getCredit(userId);
        float recalculatedDebit = getDebit(userId);
        float recalculatedRefundable = getRefundable(userId);
    
        System.out.println("Recalculating wallet for user ID: " + userId);
        System.out.println("Credit: " + recalculatedCredit + ", Debit: " + recalculatedDebit + ", Refundable: " + recalculatedRefundable);
    
        // Only update wallet balances if recalculated values differ from the stored values
        boolean walletUpdated = false;
        if (Math.abs(wallet.getCredit() - recalculatedCredit) > 0.001 && wallet.getCredit() != recalculatedCredit) {
            wallet.setCredit(recalculatedCredit);
            walletUpdated = true;
        }
    
        if (Math.abs(wallet.getDebit() - recalculatedDebit) > 0.001) {
            wallet.setDebit(recalculatedDebit);
            walletUpdated = true;
        }
    
        if (Math.abs(wallet.getRefundable() - recalculatedRefundable) > 0.001) {
            wallet.setRefundable(recalculatedRefundable);
            walletUpdated = true;
        }
    
        if (walletUpdated) {
            walletRepository.save(wallet);
            System.out.println("Updated wallet balances saved for user ID: " + userId);
        } else {
            System.out.println("No changes detected in wallet for user ID: " + userId);
        }
    }
    

    
}