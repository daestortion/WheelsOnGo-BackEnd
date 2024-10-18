package com.respo.respo.Service;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Repository.WalletRepository;
import com.respo.respo.Repository.UserRepository;
import com.respo.respo.Repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

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
        return walletRepository.findByUser_UserId(userId);
    }

    public WalletEntity updateWallet(WalletEntity walletEntity) {
        return walletRepository.save(walletEntity);
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
        List<OrderEntity> carOrders = getOrdersForOwnedCars(userId);

        if (carOrders == null || carOrders.isEmpty()) {
            System.out.println("No orders found for user ID: " + userId);
            return 0;
        }
<<<<<<< HEAD
    
        // Calculate total credit (online payments)
        float credit = (float) carOrders.stream()
            .filter(order -> ("online".equalsIgnoreCase(order.getPaymentOption()) || "PayPal".equalsIgnoreCase(order.getPaymentOption())) && !order.isTerminated())
            .mapToDouble(OrderEntity::getTotalPrice)
            .sum();
        
        System.out.println("Credit calculated for user ID: " + userId + " = " + credit);

        // Save the recalculated credit to the wallet entity
        WalletEntity wallet = walletRepository.findByUser_UserId(userId);
        if (wallet == null) {
            System.out.println("Wallet not found for user ID: " + userId);
            throw new RuntimeException("Wallet not found for user ID: " + userId);
        }

        wallet.setCredit(credit);
        walletRepository.save(wallet);
        System.out.println("Credit updated in the database for user ID: " + userId);

        return credit;
    }

=======

        // Calculate total credit (online payments)
        return (float) carOrders.stream()
                .filter(order -> "online".equalsIgnoreCase(order.getPaymentOption()) && !order.isTerminated())
                .mapToDouble(OrderEntity::getTotalPrice)
                .sum();
    }
>>>>>>> 4c948a95112a3aea462f4bd166de7a40e83c37ee

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
            System.out.println("Wallet not found for user ID: " + userId);
            throw new RuntimeException("Wallet not found for user ID: " + userId);
        }

        // Calculate credit, debit, and refundable based on the cars owned by the user
        float credit = getCredit(userId);  // Credit recalculated and saved here
        float debit = getDebit(userId);    // Debit recalculated and saved here
        float refundable = getRefundable(userId);

        // Log calculated values
        System.out.println("Recalculating wallet for user ID: " + userId);
        System.out.println("Credit: " + credit + ", Debit: " + debit + ", Refundable: " + refundable);

        // Update wallet entity with recalculated balances
        wallet.setCredit(credit);
        wallet.setDebit(debit);
        wallet.setRefundable(refundable);

        // Save the updated wallet back to the repository
        walletRepository.save(wallet);
        System.out.println("Updated wallet saved for user ID: " + userId);
    }
<<<<<<< HEAD
    
}
=======
}
>>>>>>> 4c948a95112a3aea462f4bd166de7a40e83c37ee
