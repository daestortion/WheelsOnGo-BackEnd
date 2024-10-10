package com.respo.respo.Controller;

import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping
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
}
