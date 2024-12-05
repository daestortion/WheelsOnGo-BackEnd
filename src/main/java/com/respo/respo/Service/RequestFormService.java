package com.respo.respo.Service;

import com.respo.respo.Entity.OwnerWalletEntity;
import com.respo.respo.Entity.RequestFormEntity;
import com.respo.respo.Entity.UserEntity;
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

    // Fetch all requests
    public List<RequestFormEntity> getAllRequests() {
        return requestFormRepository.findAll();
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

        // Set user and save the request
        UserEntity user = userOptional.get();
        requestForm.setUser(user);

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
}
