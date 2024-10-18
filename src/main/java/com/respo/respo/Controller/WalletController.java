package com.respo.respo.Controller;

import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
         walletService.updateWalletBalances(userId);
     }
 
     // Get credit for a specific user's wallet
     @GetMapping("/credit/{userId}")
     public float getCredit(@PathVariable int userId) {
         return walletService.getCredit(userId);
     }
 
     // Get debit for a specific user's wallet
     @GetMapping("/debit/{userId}")
     public float getDebit(@PathVariable int userId) {
         return walletService.getDebit(userId);
     }
 
     // Get refundable amount for a specific user's wallet
     @GetMapping("/refundable/{userId}")
     public float getRefundable(@PathVariable int userId) {
         return walletService.getRefundable(userId);
     }
}
