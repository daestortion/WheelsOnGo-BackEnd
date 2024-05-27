package com.respo.respo.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Repository.UserRepository;
import com.respo.respo.Service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = ("https://main--wheelsongo.netlify.app"))
public class UserController {

    @Autowired
    UserService userv;

    @Autowired
    UserRepository urepo;

    @Autowired
    private UserService userService;

    @GetMapping("/print")
    public String itWorks() {
        return "It works";
    }

    // Create
    @PostMapping("/insertUser")
    public UserEntity insertUser(@RequestBody UserEntity user) {
        return userv.insertUser(user);
    }

    // Read
    @GetMapping("/getAllUsers")
    public List<UserEntity> getAllUsers() {
        List<UserEntity> users = userv.getAllUsers();
        users.forEach(user -> System.out.println("User: " + user.getUserId() + ", isDeleted: " + user.isDeleted()));
        return users;
    }

    
    // U - Update a user record
    @PutMapping("/updateUser")
    public UserEntity updateUser(@RequestParam int userId, 
                                 @RequestParam(required = false) String pNum,
                                 @RequestParam(required = false) String email,
                                 @RequestPart(value = "profilePic", required = false) MultipartFile profilePic) {
        UserEntity user = userService.getUserById(userId);
        if (email != null && !email.isEmpty()) user.setEmail(email);
        if (pNum != null && !pNum.isEmpty()) user.setpNum(pNum);
        if (profilePic != null && !profilePic.isEmpty()) {
            try {
                byte[] bytes = profilePic.getBytes();
                user.setProfilePic(bytes);
            } catch (IOException e) {
                e.printStackTrace();  // handle the exception
            }
        }
        return userService.updateUser(userId, user);  // Ensure that this method accepts both the userId and UserEntity.
    }

    // D - Delete a user record
    @PutMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        try {
            String result = userv.deleteUser(userId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Reactivate
    @PutMapping("/reactivateUser/{userId}")
    public String reactivateUser(@PathVariable int userId) {
        return userv.reactivateUser(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String identifier = credentials.get("identifier");
        String password = credentials.get("password");

        try {
            Optional<UserEntity> user = userService.validateUser(identifier, password);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is deleted.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String identifier) {
        try {
            UserEntity user = userv.getUserByIdentifier(identifier);
            if (user != null) {
                // Assume you generate a reset token and store it with an expiry time in your database
                String resetLink = "http://localhost:3000/resetpassword";
                userv.sendPasswordResetEmail(user, resetLink);
                return ResponseEntity.ok("If the email is associated with an account, a reset link has been sent.");
            } else {
                return ResponseEntity.badRequest().body("No account associated with this email.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam int userId, @RequestParam String newPassword) {
        try {
            UserEntity user = userv.resetPassword(userId, newPassword);
            return ResponseEntity.ok("Password for user " + user.getUsername() + " has been successfully updated.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/updateIsOwner/{userId}")
    public ResponseEntity<?> updateIsRenting(@PathVariable int userId, @RequestBody Map<String, Boolean> updates) {
        try {
            UserEntity user = userService.getUserById(userId);
            if (updates.containsKey("isOwner")) {
                user.setOwner(updates.get("isOwner"));
                userService.updateUser(user);
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.badRequest().body("Invalid request");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable int userId) {
        try {
            UserEntity user = userService.getUserById(userId);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/checkDatabaseEmpty") // Corrected the mapping
    public ResponseEntity<Boolean> checkDatabaseEmpty() {
        boolean isEmpty = urepo.count() == 0;
        return ResponseEntity.ok(isEmpty);
    }
}
