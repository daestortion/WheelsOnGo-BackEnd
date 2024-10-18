package com.respo.respo.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.respo.respo.Entity.RequestFormEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Repository.RequestFormRepository;
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

     @PostMapping("/request-funds")
    public ResponseEntity<String> requestFunds(@RequestBody Map<String, Object> requestData) {
        try {
            int userId = (int) requestData.get("userId");
            String requestType = (String) requestData.get("requestType");

            // Fetch user
            UserEntity user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
            }

            // Handle GCash and Bank requests differently
            if (requestType.equals("gcash")) {
                String fullName = (String) requestData.get("fullName");
                String gcashNumber = (String) requestData.get("gcashNumber");
                float amount = Float.parseFloat(requestData.get("amount").toString());

                // Save GCash request
                RequestFormEntity requestForm = new RequestFormEntity(user, requestType, fullName, gcashNumber, null, null, amount);
                requestFormRepository.save(requestForm);

            } else if (requestType.equals("bank")) {
                String accountName = (String) requestData.get("accountName");  // Account Name is now included
                String bankName = (String) requestData.get("bankName");
                String accountNumber = (String) requestData.get("accountNumber");
                float amount = Float.parseFloat(requestData.get("amount").toString());

                // Save Bank request
                RequestFormEntity requestForm = new RequestFormEntity(user, requestType, accountName, null, bankName, accountNumber, amount);
                requestFormRepository.save(requestForm);
            }

            return ResponseEntity.ok("Request submitted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to submit the request: " + e.getMessage());
        }
    }

}
