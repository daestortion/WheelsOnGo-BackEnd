package com.respo.respo.Service;

import com.respo.respo.Entity.RequestFormEntity;
import com.respo.respo.Entity.UserEntity;
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

    // Fetch all requests
    public List<RequestFormEntity> getAllRequests() {
        return requestFormRepository.findAll();
    }

    // Create a new request
    public RequestFormEntity createRequest(int userId, RequestFormEntity requestForm) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        UserEntity user = userOptional.get();
        requestForm.setUser(user);
        return requestFormRepository.save(requestForm);
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
}
