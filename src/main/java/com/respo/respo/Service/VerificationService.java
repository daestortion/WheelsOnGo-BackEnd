package com.respo.respo.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.VerificationEntity;
import com.respo.respo.Repository.VerificationRepository;

@Service
public class VerificationService {

    @Autowired
    VerificationRepository vrepo;

    public VerificationEntity insertVerification(VerificationEntity verification) {
        return vrepo.save(verification);
    }

    public List<VerificationEntity> getAllVerifications() {
        return vrepo.findAll();
    }

    public VerificationEntity getVerificationById(int vId) {
        return vrepo.findById(vId)
                .orElseThrow(() -> new NoSuchElementException("Verification with ID " + vId + " does not exist."));
    }

    // Update
    public VerificationEntity updateVerification(int vId, VerificationEntity newVerificationDetails) {
        VerificationEntity verification = vrepo.findById(vId)
                .orElseThrow(() -> new NoSuchElementException("Verification with ID " + vId + " does not exist."));

        // Update fields accordingly
        if (newVerificationDetails.getUser() != null) {
            verification.setUser(newVerificationDetails.getUser());
        }
        verification.setStatus(newVerificationDetails.getStatus());
        verification.setGovId(newVerificationDetails.getDriversLicense());
        verification.setDriversLicense(newVerificationDetails.getGovId());

        return vrepo.save(verification);
    }

    // Delete
    public String deleteVerification(int vId) {
        VerificationEntity verification = vrepo.findById(vId)
                .orElseThrow(() -> new NoSuchElementException("Verification with ID " + vId + " does not exist"));

        vrepo.delete(verification);
        return "Verification with ID " + vId + " has been deleted";
    }

    // Utility method to change verification status
    public VerificationEntity changeVerificationStatus(int vId, int newStatus) {
        VerificationEntity verification = vrepo.findById(vId)
                .orElseThrow(() -> new NoSuchElementException("Verification with ID " + vId + " does not exist."));

        verification.setStatus(newStatus);
        return vrepo.save(verification);
    }

    public VerificationEntity getVerificationByUserId(int userId) {
        return vrepo.findByUser_UserId(userId)
            .orElseThrow(() -> new NoSuchElementException("Verification record not found for user ID: " + userId));
    }
    
}
