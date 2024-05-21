package com.respo.respo.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.respo.respo.Entity.AdminEntity;
import com.respo.respo.Repository.AdminRepository;

@Service
public class AdminService {

	@Autowired
    AdminRepository arepo;
	
	// Create
	public AdminEntity insertAdmin(AdminEntity admin) {
		return arepo.save(admin);
    }
	
	// Read
    public List<AdminEntity> getAllAdmins() {
        return arepo.findAll();
    }
    
    // Update
        public AdminEntity updateAdmin(int adminId, AdminEntity newAdminDetails) {
            AdminEntity admin = arepo.findById(adminId).orElseThrow(() ->
                new NoSuchElementException("Admin " + adminId + " does not exist!"));

            // Check for non-null and non-empty username
            if (newAdminDetails.getUsername() != null && !newAdminDetails.getUsername().isEmpty()) {
                // Ensure the new username is unique and not the current user's username
                if (!newAdminDetails.getUsername().equals(admin.getUsername()) && 
                    arepo.existsByUsername(newAdminDetails.getUsername())) {
                    throw new IllegalStateException("Username already exists. Please choose a different username.");
                }
                admin.setUsername(newAdminDetails.getUsername());
            }
                
            if (newAdminDetails.getUsername() != null && !newAdminDetails.getUsername().isEmpty()) {
                admin.setUsername(newAdminDetails.getUsername());
            }
            if (newAdminDetails.getpWord() != null && !newAdminDetails.getpWord().isEmpty()) {
                admin.setpWord(newAdminDetails.getpWord());
            }

            return arepo.save(admin);
        }
    
    // Delete
    public String deleteAdmin(int adminId) {
    	AdminEntity admin = arepo.findById(adminId)
            .orElseThrow(() -> new NoSuchElementException("Admin " + adminId + " does not exist"));

        if (admin.getisDeleted()) {
            return "Admin #" + adminId + " is already deleted!";
        } else {
            admin.setisDeleted(true);
            arepo.save(admin);
            return "Admin #" + adminId + " has been deleted";
        }
    }

	public int loginAdmin(String username, String password) {
        Optional<AdminEntity> adminOpt = arepo.findByUsername(username);
    
        if (adminOpt.isPresent() && adminOpt.get().getpWord().equals(password)) {
            return 1; // Login successful
        }
        return 0; // Login unsuccessful, either username not found or password incorrect
    }
    
    public AdminEntity getAdminByIdentifier(String username) {
        if (StringUtils.hasText(username)) {
            return arepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("No user found with username: " + username));
        }
        throw new IllegalArgumentException("Username is not provided or empty.");
    }

    public AdminEntity getAdminById(int adminId) {
        return arepo.findById(adminId).orElseThrow(() -> new NoSuchElementException("User not found with id: " + adminId));
    }
}
