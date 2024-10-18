package com.respo.respo.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Entity.VerificationEntity;
import com.respo.respo.Repository.VerificationRepository;

@Service
public class VerificationService {

    @Autowired
    VerificationRepository vrepo;

    @Autowired
    private ActivityLogService logService;

    public VerificationEntity insertVerification(VerificationEntity verification) {
        // Save the verification in the database
        VerificationEntity savedVerification = vrepo.save(verification);
        // Log the activity: "user.username submitted a verification"
        String logMessage = verification.getUser().getUsername() + " submitted a verification.";
        logService.logActivity(logMessage, verification.getUser().getUsername());
        return savedVerification;
    }

    public List<VerificationEntity> getAllVerifications() {
        return vrepo.findAll();
    }

    public VerificationEntity getVerificationById(int vId) {
        return vrepo.findById(vId)
                .orElseThrow(() -> new NoSuchElementException("Verification with ID " + vId + " does not exist."));
    }

    // Update
    public VerificationEntity updateVerification(VerificationEntity verification) {
        VerificationEntity existingVerification = vrepo.findById(verification.getVId())
                .orElseThrow(() -> new NoSuchElementException("Verification with ID " + verification.getVId() + " does not exist."));
        
        // Update fields accordingly
        existingVerification.setStatus(verification.getStatus());
        existingVerification.setGovId(verification.getGovId());
        existingVerification.setDriversLicense(verification.getDriversLicense());
    
        return vrepo.save(existingVerification);
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

        // Update verification status
        verification.setStatus(newStatus);
        VerificationEntity updatedVerification = vrepo.save(verification);

        // Get the user associated with the verification
        UserEntity user = verification.getUser();

        // Log the appropriate message based on the new status
        if (newStatus == 1) {
            // Approved: Log "user.username has been successfully verified."
            String logMessage = user.getUsername() + " has been successfully verified.";
            logService.logActivity(logMessage, user.getUsername());
        } else if (newStatus == 2) {
            // Denied: Log "The verification of user.username is unsuccessful"
            String logMessage = "The verification of " + user.getUsername() + " is unsuccessful.";
            logService.logActivity(logMessage, user.getUsername());
        }

        return updatedVerification;
    }

    public VerificationEntity getVerificationByUserId(int userId) {
        return vrepo.findByUser_UserId(userId)
            .orElseThrow(() -> new NoSuchElementException("Verification record not found for user ID: " + userId));
    }
    
}
