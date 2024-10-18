package com.respo.respo.Controller;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping; // Import MediaType class
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Entity.VerificationEntity;
import com.respo.respo.Service.UserService;
import com.respo.respo.Service.VerificationService;

@RestController
@RequestMapping("/verification")
@CrossOrigin(origins = "http://main--wheelsongo.netlify.app")
public class VerificationController {

    @Autowired
    VerificationService vserv;
    @Autowired
    UserService userv;
    
    @GetMapping("/test")
    public String test() {
        return "Verification service is running!";
    }

    @PostMapping("/insertVerification")
    public VerificationEntity insertVerification(@RequestParam("userId") int userId, 
                                                 @RequestParam("status") int status, 
                                                 @RequestParam("govId") MultipartFile govId, 
                                                 @RequestParam("driversLicense") MultipartFile driversLicense) throws IOException {
        UserEntity userEntity = userv.getUserById(userId);
        if (userEntity == null) {
            throw new NoSuchElementException("No user found with ID: " + userId);
        }
        byte[] govIdBytes = govId.getBytes();
        byte[] driversLicenseBytes = driversLicense.getBytes();
        VerificationEntity verification = new VerificationEntity();  // Create an empty VerificationEntity
        verification.setUser(userEntity);
        verification.setStatus(status);
        verification.setGovId(govIdBytes);
        verification.setDriversLicense(driversLicenseBytes);
        
        return vserv.insertVerification(verification);
    }


    @GetMapping("/getAllVerification")
    public List<VerificationEntity> getAllVerifications() {
        return vserv.getAllVerifications();
    }    

    @GetMapping("/{vId}/govId")
    public ResponseEntity<byte[]> getGovIdImage(@PathVariable int vId) {
        VerificationEntity verification = vserv.getVerificationById(vId);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(verification.getGovId());
    }
    
    @GetMapping("/{vId}/driversLicense")
    public ResponseEntity<byte[]> getDriversLicenseImage(@PathVariable int vId) {
        VerificationEntity verification = vserv.getVerificationById(vId);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(verification.getDriversLicense());
    }    

    // Update an existing verification
    @PutMapping("/updateVerification/{userId}")
    public VerificationEntity updateVerification(@PathVariable int userId, 
                                             @RequestParam("status") int status, 
                                             @RequestParam("govId") MultipartFile govId, 
                                             @RequestParam("driversLicense") MultipartFile driversLicense) throws IOException {
        UserEntity userEntity = userv.getUserById(userId);
        if (userEntity == null) {
            throw new NoSuchElementException("No user found with ID: " + userId);
        }
    
        VerificationEntity verification = vserv.getVerificationByUserId(userId);
        if (verification == null) {
            throw new NoSuchElementException("No verification record found for user with ID: " + userId);
        }

        byte[] govIdBytes = govId.getBytes();
        byte[] driversLicenseBytes = driversLicense.getBytes();
        verification.setStatus(status);
        verification.setGovId(govIdBytes);
        verification.setDriversLicense(driversLicenseBytes);

        return vserv.updateVerification(verification);
    }


    // Delete a verification
    @DeleteMapping("/deleteVerification/{vId}")
    public String deleteVerification(@PathVariable int vId) {
        return vserv.deleteVerification(vId);
    }

    @PutMapping("/changeStatus/{vId}")
    public VerificationEntity changeVerificationStatus(@PathVariable int vId, @RequestParam int newStatus) {
        return vserv.changeVerificationStatus(vId, newStatus);
    }
    
    @GetMapping("/getVerificationByUserId/{userId}")
    public ResponseEntity<?> getVerificationByUserId(@PathVariable int userId) {
        try {
            VerificationEntity verification = vserv.getVerificationByUserId(userId);
            return ResponseEntity.ok(verification);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Verification record not found for user ID: " + userId);
        }
    }

}