package com.respo.respo.Service;

import com.respo.respo.Entity.OwnerWalletEntity;
import com.respo.respo.Entity.RequestFormEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Repository.OwnerWalletRepository;
import com.respo.respo.Repository.RequestFormRepository;
import com.respo.respo.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RequestFormService {

    @Autowired
    private RequestFormRepository requestFormRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OwnerWalletRepository ownerWalletRepository;

    @Autowired
    private WalletService walletService;  // To fetch wallet balance for renters


    // Fetch all requests
    public List<RequestFormEntity> getAllRequests() {
        List<RequestFormEntity> requests = requestFormRepository.findAll();
        
        // Check that the userType is being set correctly here
        requests.forEach(request -> System.out.println("User Type: " + request.getUserType()));
        
        return requests;
    }    


    public RequestFormEntity createRequest(int userId, RequestFormEntity requestForm) {
        // Check if user exists
        Optional<UserEntity> userOptional = userRepository.findById(userId);
    
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
    
        // Fetch the wallet details of the user
        OwnerWalletEntity wallet = ownerWalletRepository.findByUserUserId(userId);
    
        // Check if the requested amount is greater than the available online earnings
        if (requestForm.getAmount() > wallet.getOnlineEarning()) {
            throw new IllegalArgumentException("Requested amount exceeds available online earnings.");
        }
    
        // Set user and userType
        UserEntity user = userOptional.get();
        requestForm.setUser(user);
        
        // Set userType based on requestType (check here)
        if (requestForm.getRequestType().equals("Refund")) {
            requestForm.setUserType("Renter");
        } else {
            requestForm.setUserType("Car Owner");
        }
    
        try {
            return requestFormRepository.save(requestForm);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request: " + e.getMessage());
        }
    }

    // Fetch requests by userId
    public List<RequestFormEntity> getRequestsByUserId(int userId) {
        return requestFormRepository.findAllByUser_UserId(userId);
    }

    // Approve a request by setting its status to "approved"
    public RequestFormEntity approveRequest(int requestId) {
        Optional<RequestFormEntity> optionalRequest = requestFormRepository.findById(requestId);

        if (optionalRequest.isEmpty()) {
            throw new IllegalArgumentException("Request not found with ID: " + requestId);
        }

        RequestFormEntity request = optionalRequest.get();
        request.setStatus("approved");
        return requestFormRepository.save(request);
    }

    public RequestFormEntity getRequestById(int requestId) {
    return requestFormRepository.findById(requestId)
            .orElseThrow(() -> new NoSuchElementException("Request with ID " + requestId + " not found."));
    }

    public RequestFormEntity updateRequest(int requestId, RequestFormEntity newRequestFormDetails) {
        RequestFormEntity request = requestFormRepository.findById(requestId).orElseThrow(() ->
            new NoSuchElementException("Request " + requestId + " does not exist!"));
    
        if (newRequestFormDetails.getProofImage() != null && newRequestFormDetails.getProofImage().length > 0) {
            // Update the proof image
            request.setProofImage(newRequestFormDetails.getProofImage());
        }
    
        // Save the updated request entity
        return requestFormRepository.save(request);
    }

    // New service method to handle the refund request
    public String requestRefund(int userId, RequestFormEntity requestForm) {
        // Fetch the renter's wallet balance
        WalletEntity walletEntity = walletService.getWalletByUserId(userId);
        if (walletEntity == null) {
            throw new IllegalArgumentException("Wallet not found for userId: " + userId);
        }

        double availableBalance = walletEntity.getBalance();

        // Check if the requested refund amount exceeds the available balance
        if (requestForm.getAmount() > availableBalance) {
            throw new IllegalArgumentException("Requested amount exceeds available balance.");
        }

        // Set the user for the refund request
        UserEntity user = walletEntity.getUser();
        requestForm.setUser(user);
        requestForm.setStatus("pending");  // Set the request status to "pending"

        // Save the refund request to the database
        try {
            requestFormRepository.save(requestForm);
            return "Refund request created successfully.";
        } catch (Exception e) {
            throw new RuntimeException("Failed to create refund request: " + e.getMessage());
        }
    }

    public String validateRenterRefund(int userId, RequestFormEntity requestForm) {
        // Fetch the renter's wallet and total refundable amount
        WalletEntity walletEntity = walletService.getWalletByUserId(userId);
        if (walletEntity == null) {
            throw new IllegalArgumentException("Wallet not found for userId: " + userId);
        }
    
        double availableBalance = walletEntity.getBalance();
        double refundableAmount = walletService.getTotalRefundableAmount(userId);  // Get total refundable amount
    
        // Check if the requested refund exceeds the available balance or refundable amount
        if (requestForm.getAmount() > availableBalance || requestForm.getAmount() > refundableAmount) {
            throw new IllegalArgumentException("Requested refund exceeds available balance or refundable amount.");
        }
    
        return "Refund request is valid.";
    }    
    
    public RequestFormEntity approveRenterRefund(int requestId) {
        Optional<RequestFormEntity> optionalRequest = requestFormRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            throw new IllegalArgumentException("Request not found with ID: " + requestId);
        }
    
        RequestFormEntity request = optionalRequest.get();
        
        // Deduct from renter's wallet balance
        WalletEntity walletEntity = walletService.getWalletByUserId(request.getUser().getUserId());
        if (walletEntity != null) {
            double availableBalance = walletEntity.getBalance();
            if (request.getAmount() <= availableBalance) {
                walletEntity.setBalance(availableBalance - request.getAmount());
                walletService.save(walletEntity);  // Now this should work, saving the updated wallet
            } else {
                throw new IllegalArgumentException("Insufficient balance to process refund.");
            }
        }
    
        // Update status and return the approved request
        request.setStatus("approved");
        return requestFormRepository.save(request);
    }
    
}
