        package com.respo.respo.Service;

        import java.io.IOException;
        import java.util.Base64;
        import java.util.Collections;
        import java.util.List;
        import java.util.NoSuchElementException;
        import java.util.Optional;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.mail.SimpleMailMessage;
        import org.springframework.mail.javamail.JavaMailSender;
        import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

        import com.respo.respo.Entity.OrderEntity;
        import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Repository.UserRepository;

        @Service
        public class UserService {

            @Autowired
            UserRepository urepo;

            @Autowired
            private JavaMailSender mailSender;

            @Autowired
            private ActivityLogService logService;
            
            private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // Create
            public UserEntity insertUser(UserEntity user) throws IllegalArgumentException {
                // Check if username or email already exists
                if (urepo.existsByUsername(user.getUsername())) {
                    throw new IllegalArgumentException("Username already exists.");
                }
                if (urepo.findByEmail(user.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("Email already registered.");
                }
                // Hash the password before saving it
                String encodedPassword = passwordEncoder.encode(user.getpWord());
                user.setpWord(encodedPassword); // Set the hashed password
                
                UserEntity savedUser = urepo.save(user);
                
                // Log the user creation
                logService.logActivity("User " + savedUser.getUsername() + " has registered to WheelsOnGo.", savedUser.getUsername());

                return savedUser;  // Return the saved user
            }

            public boolean checkPassword(String rawPassword, String encodedPassword) {
                return passwordEncoder.matches(rawPassword, encodedPassword);
            }
            
            // Read
            public List<UserEntity> getAllUsers() {
                List<UserEntity> users = urepo.findAll();
                users.forEach(user -> System.out.println("Fetched user: " + user)); // Log user data
                return users;
            }

            // Update
            public UserEntity updateUser(int userId, UserEntity newUserDetails) {
                UserEntity user = urepo.findById(userId).orElseThrow(() ->
                    new NoSuchElementException("User " + userId + " does not exist!"));

                // Check for non-null and non-empty username
                if (newUserDetails.getUsername() != null && !newUserDetails.getUsername().isEmpty()) {
                    // Ensure the new username is unique and not the current user's username
                    if (!newUserDetails.getUsername().equals(user.getUsername()) && 
                        urepo.existsByUsername(newUserDetails.getUsername())) {
                        throw new IllegalStateException("Username already exists. Please choose a different username.");
                    }
                    user.setUsername(newUserDetails.getUsername());
                }

                if (newUserDetails.getEmail() != null && !newUserDetails.getEmail().isEmpty()) {
                    user.setEmail(newUserDetails.getEmail());
                }
                if (newUserDetails.getpWord() != null && !newUserDetails.getpWord().isEmpty()) {
                    user.setpWord(newUserDetails.getpWord());
                }
                if (newUserDetails.getpNum() != null && !newUserDetails.getpNum().isEmpty()) {
                    user.setpNum(newUserDetails.getpNum());
                }
                if (newUserDetails.getProfilePic() != null && newUserDetails.getProfilePic().length > 0) {
                    // Update the user's profile pic
                    user.setProfilePic(newUserDetails.getProfilePic());
                }

                // Save the updated user entity
                UserEntity updatedUser = urepo.save(user);
        
                // Log the user profile update activity
                String logMessage = updatedUser.getUsername() + " updated their profile.";
                logService.logActivity(logMessage, updatedUser.getUsername());
        
                return updatedUser;
            }

            // Delete
            public String deleteUser(int userId) {
                UserEntity user = urepo.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User " + userId + " does not exist"));
            
                if (user.isDeleted()) {
                    return "User #" + userId + " is already deleted!";
                } else {
                    user.setDeleted(true);
                    urepo.save(user);
                    return "User #" + userId + " has been deleted";
                }
            }
            

            // Reactivate
            public String reactivateUser(int userId) {
                UserEntity user = urepo.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User " + userId + " does not exist"));

                if (!user.isDeleted()) {
                    return "User #" + userId + " is not deleted!";
                } else {
                    user.setDeleted(false);
                    urepo.save(user);
                    return "User #" + userId + " has been reactivated";
                }
            }

            public int loginUser(String identifier, String password) {
                Optional<UserEntity> userOpt = identifier.contains("@") ? 
                    urepo.findByEmail(identifier) : urepo.findByUsername(identifier);

                if (userOpt.isPresent() && checkPassword(password, userOpt.get().getpWord())) {
                    return 1; // Login successful
                }
                return 0; // Login unsuccessful, either identifier not found or password incorrect
            }    
        
            
            public UserEntity getUserByIdentifier(String identifier) {
                if (StringUtils.hasText(identifier)) {
                    if (identifier.contains("@")) {
                        return urepo.findByEmail(identifier)
                                    .orElseThrow(() -> new IllegalArgumentException("No user found with email: " + identifier));
                    } else {
                        return urepo.findByUsername(identifier)
                                    .orElseThrow(() -> new IllegalArgumentException("No user found with username: " + identifier));
                    }
                }
                throw new IllegalArgumentException("Identifier is not provided or empty.");
            }
            
            public UserEntity resetPassword(int userId, String newPassword) {
                UserEntity user = urepo.findById(userId).orElseThrow(() ->
                    new NoSuchElementException("No user found with ID: " + userId));
                
                String hashedPassword = passwordEncoder.encode(newPassword);
                user.setpWord(hashedPassword);  // Update the password
                urepo.save(user);
                return user;
            }
            public void sendPasswordResetEmail(UserEntity user, String resetLink) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("ayadekenneth07@gmail.com");
                message.setTo(user.getEmail());
                message.setSubject("Password Reset Request");
                message.setText("To reset your password, click the following link: " + resetLink);
                try {
                    mailSender.send(message);
                    System.out.println("Email sent successfully to " + user.getEmail());
                } catch (Exception e) {
                    System.out.println("Failed to send email to " + user.getEmail());
                    e.printStackTrace();
                }
            }

            public Optional<UserEntity> validateUser(String identifier, String password) {
                Optional<UserEntity> user = identifier.contains("@")
                        ? urepo.findByEmail(identifier)
                        : urepo.findByUsername(identifier);

                if (user.isPresent()) {
                    UserEntity userEntity = user.get();
                    // Use BCryptPasswordEncoder's matches method to check the password
                    if (passwordEncoder.matches(password, userEntity.getpWord()) && !userEntity.isDeleted()) {
                        return user;
                    } else if (userEntity.isDeleted()) {
                        throw new IllegalStateException("Account is deleted.");
                    }
                }
                return Optional.empty();
            }
            
            

            public UserEntity getUserById(int userId) {
                return urepo.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
            }

            // Method to convert MultipartFile to byte array (to save in DB)
            public byte[] convertToBlob(MultipartFile file) {
                try {
                    return file.getBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                    return new byte[0];
                }
            }

            // Method to convert byte array back to Base64 string (to send to frontend)
            public String convertBlobToBase64(byte[] blob) {
                return Base64.getEncoder().encodeToString(blob);
            }

            public UserEntity updateUser(UserEntity user) {
                int userId = user.getUserId(); // Assuming the userId is set in the UserEntity object
                UserEntity existingUser = urepo.findById(userId).orElseThrow(() ->
                    new NoSuchElementException("User not found with id: " + userId));
                
                // Perform updates on existingUser with the details from `user`
                existingUser.setRenting(user.isRenting());
                // add other fields as needed
                
                return urepo.save(existingUser);
            }
            
            public List<OrderEntity> getAllOrdersByUserId(int userId) {
                Optional<UserEntity> userOpt = urepo.findById(userId);
                if (userOpt.isPresent()) {
                    return userOpt.get().getOrders(); // Get orders directly from the UserEntity
                }
                return Collections.emptyList(); // Return an empty list if user is not found
            }

            public UserEntity updateIsOwner(int userId, boolean isOwner) {
                UserEntity user = urepo.findById(userId).orElseThrow(() ->
                    new NoSuchElementException("User with ID " + userId + " does not exist"));
        
                // Update the isOwner status
                user.setOwner(isOwner);
                UserEntity updatedUser = urepo.save(user);
        
                // Log the action if the user has applied to become an owner
                if (isOwner) {
                    String logMessage = user.getUsername() + " has applied as owner.";
                    logService.logActivity(logMessage, user.getUsername());
                }
        
                return updatedUser;
            }
}