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

    public WalletEntity updateWallet(WalletEntity walletEntity) {
        return walletRepository.save(walletEntity);
    }

    public void deleteWallet(int id) {
        walletRepository.deleteById(id);
    }

    // Method to get paid orders for a specific user
    public List<OrderEntity> getPaidOrdersForUser(int userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null; // User not found
        }

        // Get all cars owned by the user
        List<CarEntity> ownedCars = user.getCars();

        // Get orders for each car where isPaid is true
        List<OrderEntity> paidOrders = ownedCars.stream()
                .flatMap(car -> car.getOrders().stream()) // Get orders for each car
                .filter(order -> order.isPaid()) // Filter orders with isPaid = true
                .collect(Collectors.toList());

        return paidOrders;
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional // Ensure this method runs within a transaction
    public void scanPaidOrdersForAllOwners() {
        // Retrieve all users
        List<UserEntity> allOwners = userRepository.findAll()
            .stream()
            .filter(UserEntity::isOwner) // Filter users who are owners
            .collect(Collectors.toList());

        allOwners.forEach(user -> {
            List<OrderEntity> paidOrders = getPaidOrdersForUser(user.getUserId());
            // Process paid orders, for example, update wallet balances
            System.out.println("Paid orders for user " + user.getUsername() + ": " + paidOrders.size());
        });
    }

}
