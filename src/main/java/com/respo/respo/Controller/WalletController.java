package com.respo.respo.Controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Repository.RequestFormRepository;
import com.respo.respo.Repository.WalletRepository;
import com.respo.respo.Service.UserService;
import com.respo.respo.Service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;  // Inject UserService to fetch use

    @Autowired
    private RequestFormRepository requestFormRepository;


    @GetMapping("/all")
    public List<WalletEntity> getAllWallets() {
        return walletService.getAllWallets();
    }
    @GetMapping("/{id}")
    public WalletEntity getWalletById(@PathVariable int id) {
        return walletService.getWalletById(id);
    }
    @PostMapping
    public WalletEntity createWallet(@RequestBody WalletEntity walletEntity) {
        return walletService.createWallet(walletEntity);
    }

    @PutMapping("/refund/{orderId}")
    public ResponseEntity<String> processRefund(@PathVariable int orderId) {
        try {
            double refundedAmount = walletService.processRefund(orderId);
            return ResponseEntity.ok("Refund processed successfully. Amount: ₱" + refundedAmount);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing refund: " + e.getMessage());
        }
    }    

}
