package com.respo.respo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

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
    @PutMapping("/{id}")
    public WalletEntity updateWallet(@PathVariable int id, @RequestBody WalletEntity walletEntity) {
        walletEntity.setWalletId(id);
        return walletService.updateWallet(walletEntity);
    }
    @DeleteMapping("/{id}")
    public void deleteWallet(@PathVariable int id) {
        walletService.deleteWallet(id);
    }
     // Recalculate wallet balances (credit, debit, refundable) for a specific user by user ID
     @PutMapping("/recalculate/{userId}")
     public void recalculateWalletBalances(@PathVariable int userId) {
         System.out.println("API called to recalculate wallet for user ID: " + userId);
         walletService.updateWalletBalances(userId);
         System.out.println("Wallet recalculated successfully for user ID: " + userId);
     }
 
     // Get credit for a specific user's wallet
     @GetMapping("/credit/{userId}")
     public float getCredit(@PathVariable int userId) {
         System.out.println("API called to fetch credit for user ID: " + userId);
         float credit = walletService.getCredit(userId);
         System.out.println("Credit fetched for user ID: " + userId + " = " + credit);
         return credit;
     }
 
     // Get debit for a specific user's wallet
     @GetMapping("/debit/{userId}")
     public float getDebit(@PathVariable int userId) {
         System.out.println("API called to fetch debit for user ID: " + userId);
         float debit = walletService.getDebit(userId);
         System.out.println("Debit fetched for user ID: " + userId + " = " + debit);
         return debit;
     }
 
     // Get refundable amount for a specific user's wallet
     @GetMapping("/refundable/{userId}")
     public float getRefundable(@PathVariable int userId) {
         return walletService.getRefundable(userId);
     }
}
