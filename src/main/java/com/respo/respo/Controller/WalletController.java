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

        // Add a new endpoint to fetch all payment requests
        @GetMapping("/getAllRequests")
        public List<RequestFormEntity> getAllRequests() {
            // Log for debugging
            System.out.println("API called to fetch all payment requests.");
            
            // Fetch all requests from the RequestFormRepository
            return requestFormRepository.findAll();
        }

        @PutMapping("/approveRequest/{requestId}")
        public ResponseEntity<String> approveRequest(@PathVariable int requestId) {
            System.out.println("Approving request ID: " + requestId);
        
            try {
                // Fetch the request from the repository
                RequestFormEntity request = requestFormRepository.findById(requestId).orElse(null);
                if (request == null) {
                    System.out.println("Request not found for ID: " + requestId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found");
                }
        
                System.out.println("Fetched request, checking wallet for user ID: " + request.getUser().getUserId());
        
                // Fetch the user's wallet
                WalletEntity wallet = walletService.getWalletByUserId(request.getUser().getUserId());
                if (wallet == null || wallet.getUser() == null) {
                    System.out.println("User wallet not found or wallet is not associated with a user");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User wallet not found or wallet is not associated with a user");
                }
        
                System.out.println("Wallet found, current credit: " + wallet.getCredit() + ", amount requested: " + request.getAmount());
        
                // Deduct the amount from the user's credit balance
                float newCredit = wallet.getCredit() - request.getAmount();
                if (newCredit < 0) {
                    System.out.println("Insufficient credit balance. Cannot approve request.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient credit balance");
                }
        
                wallet.setCredit(newCredit);
                walletService.updateWallet(wallet);  // Save the updated wallet balance
        
                // Update request status to approved
                request.setStatus("approved");
                requestFormRepository.save(request);
                System.out.println("Request status updated to approved for request ID: " + requestId);
        
                return ResponseEntity.ok("Request approved successfully");
            } catch (Exception e) {
                System.out.println("Error during approval process: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error approving request: " + e.getMessage());
            }
        }
        
        
    
        // Endpoint to deny request
        @PutMapping("/denyRequest/{requestId}")
        public ResponseEntity<String> denyRequest(@PathVariable int requestId) {
            System.out.println("Denying request ID: " + requestId);
    
            // Fetch the request from the repository
            RequestFormEntity request = requestFormRepository.findById(requestId).orElse(null);
            if (request == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found");
            }
    
            // Update request status to denied
            request.setStatus("denied");
            requestFormRepository.save(request);
    
            System.out.println("Request denied successfully for ID: " + requestId);
            return ResponseEntity.ok("Request denied successfully");
        }
}
