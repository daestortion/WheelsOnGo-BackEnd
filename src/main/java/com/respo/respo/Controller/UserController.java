package com.respo.respo.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.OwnerWalletEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Repository.UserRepository;
import com.respo.respo.Service.OwnerWalletService;
import com.respo.respo.Service.UserService;
import com.respo.respo.Service.WalletService;

import java.util.Optional;

import javax.mail.MessagingException;

import com.respo.respo.Configuration.TokenGenerator; // Import TokenGenerator class

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

    @Autowired
    private OrderRepository orepo;

    @Autowired
    private WalletService walletService;

    @Autowired
    private OwnerWalletService ownerWalletService;

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
    public ResponseEntity<?> updateUser(@RequestParam int userId, 
                                 @RequestParam(required = false) String pNum,
                                 @RequestParam(required = false) String email,
                                 @RequestPart(value = "profilePic", required = false) MultipartFile profilePic) {
        UserEntity user = userService.getUserById(userId);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
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
        // Call the service to update the user and log the profile update
        UserEntity updatedUser = userService.updateUser(userId, user);
        // Return the updated user with a success response
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
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
                // Generate a reset token and store it with an expiry time in your database
                String resetToken = TokenGenerator.generateResetToken(user.getUserId()); // Using TokenGenerator to generate reset token
                String resetLink = "https://wheels-on-go-front-end.vercel.app/resetpassword?userId=" + user.getUserId() + "&token=" + resetToken;
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
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        try {
            int userId = Integer.parseInt(requestBody.get("userId"));
            String newPassword = requestBody.get("newPassword");
            
            UserEntity user = userv.resetPassword(userId, newPassword);
            return ResponseEntity.ok("Password for user " + user.getUsername() + " has been successfully updated.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NumberFormatException | NullPointerException e) {
            return ResponseEntity.badRequest().body("Invalid userId provided");
        }
    }

@PutMapping("/updateIsOwner/{userId}")
public ResponseEntity<?> updateIsOwner(@PathVariable int userId, @RequestBody Map<String, Boolean> updates) {
    try {
        // Get the user by ID
        UserEntity user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Check if "isOwner" key exists in the request body
        if (updates.containsKey("isOwner")) {
            boolean isOwner = updates.get("isOwner");

            // Update the isOwner flag
            userService.updateIsOwner(userId, isOwner);

            // If the user is now an owner and doesn't already have an OwnerWallet, create one
            if (isOwner && user.getOwnerWallet() == null) {
                OwnerWalletEntity ownerWallet = new OwnerWalletEntity();  // Create a new OwnerWallet
                ownerWallet.setUser(user);  // Associate the wallet with the user

                ownerWalletService.createOrUpdateWallet(ownerWallet);  // Save the OwnerWallet

                // Update the user's OwnerWallet reference and save the user again
                user.setOwnerWallet(ownerWallet);
                userService.updateUser(user);
            }

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
    
    @GetMapping("/getAllOrdersFromUser/{userId}")
    public ResponseEntity<List<OrderEntity>> getAllOrdersFromUser(@PathVariable int userId) {
        try {
            List<OrderEntity> orders = userv.getAllOrdersByUserId(userId);
            if (orders.isEmpty()) {
                return ResponseEntity.noContent().build(); // No orders found
            }
            return ResponseEntity.ok(orders); // Return the list of orders
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null); // Internal server error
        }
    }
    
    @GetMapping("/getOwnedCarsByUserId/{userId}")
    public ResponseEntity<List<CarEntity>> getOwnedCarsByUserId(@PathVariable int userId) {
        try {
            UserEntity user = userService.getUserById(userId);
            if (user != null) {
                List<CarEntity> ownedCars = user.getCars();
                if (ownedCars.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No cars found
                }
                return new ResponseEntity<>(ownedCars, HttpStatus.OK); // Return the list of owned cars
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // User not found
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Internal server error
        }
    }
    

    @GetMapping("/{userId}/carOrders")
    public ResponseEntity<List<OrderEntity>> getCarOrdersByUserId(@PathVariable int userId) {
        try {
            UserEntity user = userService.getUserById(userId);
            if (user != null) {
                List<CarEntity> ownedCars = user.getCars();
                List<OrderEntity> allOrders = new ArrayList<>();
                for (CarEntity car : ownedCars) {
                    allOrders.addAll(car.getOrders());
                }
                if (allOrders.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No orders found
                }
                // Sort orders by orderId in descending order to show newest orders at the top
                Collections.sort(allOrders, new Comparator<OrderEntity>() {
                    @Override
                    public int compare(OrderEntity o1, OrderEntity o2) {
                        return Integer.compare(o2.getOrderId(), o1.getOrderId());
                    }
                });
                return new ResponseEntity<>(allOrders, HttpStatus.OK); // Return all orders from all cars owned by the user
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // User not found
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Internal server error
        }
    }

    @PostMapping("/logUserAction")
    public ResponseEntity<String> logUserAction(@RequestBody Map<String, Object> logData) {
        System.out.println("Log User Action:");
        System.out.println("Action: " + logData.get("action"));
        System.out.println("Timestamp: " + logData.get("timestamp"));
        System.out.println("User Data: " + logData.get("userData"));
        
        // You can extend this to save the logs into a file, database, etc.
        
        return new ResponseEntity<>("User action logged successfully", HttpStatus.OK);
    }

 /**
     * Endpoint to send activation email.
     * 
     * @param userId The user's ID to send the activation email to.
     * @return ResponseEntity with a message.
     */
    @PostMapping("/send-activation-email/{userId}")
    public ResponseEntity<String> sendActivationEmail(@PathVariable("userId") int userId) {
        try {
            userService.sendActivationEmail(userId);
            return ResponseEntity.ok("Activation email sent successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("User not found.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            // Handle other possible exceptions
            return ResponseEntity.status(500).body("Failed to send activation email.");
        }
    }

    /**
     * Endpoint to activate a user's account using the token.
     * 
     * @param userId The user's ID.
     * @param token The activation token.
     * @return ResponseEntity with a message.
     */
    @GetMapping("/activate/{userId}/{token}")
    public ResponseEntity<String> activateUser(@PathVariable("userId") int userId,
                                               @PathVariable("token") String token) {
        try {
            UserEntity activatedUser = userService.activateUser(userId, token);
            return ResponseEntity.ok("User activated successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("User not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    
    @PostMapping("/resend-activation-email/{userId}")
    public ResponseEntity<String> resendActivationEmail(@PathVariable("userId") int userId) {
        try {
            userService.sendActivationEmail(userId);  // Reuse the sendActivationEmail method
            return ResponseEntity.ok("Activation email resent successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("User not found.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to resend activation email.");
        }
    }
}


