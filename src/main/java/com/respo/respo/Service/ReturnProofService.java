package com.respo.respo.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.ReturnProofEntity;
import com.respo.respo.Entity.WalletEntity;
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Repository.ReturnProofRepository;
import com.respo.respo.Repository.WalletRepository;

@Service
public class ReturnProofService {

    private final ReturnProofRepository returnProofRepository;
    private final OrderRepository orderRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public ReturnProofService(ReturnProofRepository returnProofRepository, OrderRepository orderRepository,
            WalletRepository walletRepository) {
        this.returnProofRepository = returnProofRepository;
        this.orderRepository = orderRepository;
        this.walletRepository = walletRepository;
    }

    public ReturnProofEntity createReturnProof(ReturnProofEntity returnProof, int orderId) {
        // Fetch the order entity
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Set the return proof's order
        returnProof.setOrder(order);
        returnProof.setEndDate(order.getEndDate()); // Set endDate from OrderEntity to ReturnProofEntity

        // Set penalty based on return date and order end date
        if (returnProof.getReturnDate() != null && order.getEndDate() != null) {
            long daysLate = ChronoUnit.DAYS.between(order.getEndDate(), returnProof.getReturnDate());
            if (daysLate > 0) {
                returnProof.setPenalty(daysLate * 500); // Set penalty as 500 multiplied by days late
            } else {
                returnProof.setPenalty(0); // No penalty if return is on time or early
            }
        } else {
            returnProof.setPenalty(0); // Default to no penalty if dates are missing
        }

        // Step to deactivate the order after return
        order.setActive(false); // Mark the order as inactive
        orderRepository.save(order); // Save the updated order entity

        // Save and return the return proof
        return returnProofRepository.save(returnProof);
    }

    public List<ReturnProofEntity> getAllReturnProofs() {
        return returnProofRepository.findAll();
    }

    public Optional<ReturnProofEntity> getReturnProofById(int id) {
        return returnProofRepository.findById(id);
    }

    public ReturnProofEntity createReturnProof(ReturnProofEntity returnProof) {
        return returnProofRepository.save(returnProof);
    }

    public ReturnProofEntity updateReturnProof(int id, ReturnProofEntity updatedReturnProof) {
        OrderEntity order = updatedReturnProof.getOrder();
    
        return returnProofRepository.findById(id)
                .map(existingProof -> {
                    // Update ReturnProofEntity fields
                    existingProof.setProof(updatedReturnProof.getProof());
                    existingProof.setRemarks(updatedReturnProof.getRemarks());
                    existingProof.setReturnDate(updatedReturnProof.getReturnDate());
                    existingProof.setEndDate(updatedReturnProof.getEndDate());
                    existingProof.setPenalty(updatedReturnProof.getPenalty());
    
                    // Update associated OrderEntity fields
                    if (order != null) {
                        order.setStatus(3); // Set status to 3
                        order.setActive(false); // Set isActive to false
                    }
    
                    // Save the updated OrderEntity if necessary
                    if (order != null) {
                        orderRepository.save(order);
                    }
    
                    // Save and return the updated ReturnProofEntity
                    return returnProofRepository.save(existingProof);
                })
                .orElseThrow(() -> new RuntimeException("ReturnProof not found"));
    }
    

    public void deleteReturnProof(int id) {
        returnProofRepository.deleteById(id);
    }

    public Map<String, Object> getRenterDetails(int orderId) {
        ReturnProofEntity proof = returnProofRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Return proof not found for order ID: " + orderId));
    
        Map<String, Object> response = new HashMap<>();
        response.put("carOwner", proof.getOrder().getCar().getOwner().getfName() + " "
                + proof.getOrder().getCar().getOwner().getlName());
        response.put("renter", proof.getOrder().getUser().getfName() + " " + proof.getOrder().getUser().getlName());
        response.put("rentStartDate", proof.getOrder().getStartDate());
        response.put("rentEndDate", proof.getOrder().getEndDate());
        response.put("carReturnDate", proof.getReturnDate());
        response.put("remarks", proof.getRemarks());
    
        // Access the ownerRemark from ReturnProofEntity
        String ownerRemark = proof.getOwnerRemark();  // Now correctly accessing ownerRemark from ReturnProofEntity
        response.put("ownerRemark", ownerRemark != null ? ownerRemark : "No remark available");
    
        // Check for null before encoding to Base64
        if (proof.getProof() != null) {
            response.put("proof", Base64.getEncoder().encodeToString(proof.getProof())); // Convert the byte array to Base64
        } else {
            response.put("proof", "No proof available");
        }
    
        if (proof.getOwnerProof() != null) {
            response.put("ownerProof", Base64.getEncoder().encodeToString(proof.getOwnerProof())); // Owner proof
        } else {
            response.put("ownerProof", "No owner proof available");
        }
    
        // Add owner approval and penalty to the response
        response.put("ownerApproval", proof.isOwnerApproval() ? "Approved" : "Not Approved");
        response.put("penalty", proof.getPenalty() > 0 ? proof.getPenalty() : "No Penalty");
    
        return response;
    }    

    public ReturnProofEntity updateOwnerProofAndOrder(int orderId, MultipartFile ownerProof,
            String ownerRemark, boolean ownerApproval)
            throws IOException {
        try {
            // Fetch the return proof and order using the orderId
            ReturnProofEntity proof = returnProofRepository.findByOrder_OrderId(orderId)
                    .orElse(null); // If not found, return null

            if (proof == null) {
                // Return error message if return proof is not found
                System.out.println("Error: Return proof not found for order ID: " + orderId);
                return null;
            }

            OrderEntity order = proof.getOrder(); // Get the associated order entity

            // Update the acknowledgment fields in the return proof
            proof.setOwnerProof(ownerProof.getBytes());
            proof.setOwnerRemark(ownerRemark);
            proof.setOwnerApproval(ownerApproval);

            // If the acknowledgment is approved, update the order
            if (ownerApproval) {
                order.setReturned(true);
                order.setReturnDate(LocalDate.now());
                orderRepository.save(order); // Save the updated order to mark it as returned
            }

            // Deduct penalty from the renter's wallet if there is one
            if (proof.getPenalty() > 0) {
                // Fetch the renter's wallet using the WalletRepository
                WalletEntity renterWallet = walletRepository.findByUser_UserId(order.getUser().getUserId());

                if (renterWallet == null) {
                    // Log and return error message if wallet is not found
                    System.out
                            .println("Error: Wallet not found for renter with user ID: " + order.getUser().getUserId());
                    return null;
                }

                // Deduct the penalty, allowing the balance to go negative
                double updatedBalance = renterWallet.getBalance() - proof.getPenalty();
                renterWallet.setBalance(updatedBalance);

                // Save the updated wallet balance
                walletRepository.save(renterWallet);

                // Log the penalty deduction (optional)
                System.out.println(
                        "Penalty of " + proof.getPenalty() + " has been deducted. New balance: " + updatedBalance);
            }

            // Save and return the updated return proof
            return returnProofRepository.save(proof);

        } catch (Exception e) {
            // Catch any other exceptions and log them
            System.out.println("An error occurred while processing the return proof: " + e.getMessage());
            return null;
        }
    }

    public boolean returnProofExists(int orderId) {
        return returnProofRepository.findByOrder_OrderId(orderId).isPresent();
    }

    public boolean isOwnerAcknowledged(int orderId) {
        return returnProofRepository.findByOrder_OrderId(orderId)
                .map(ReturnProofEntity::isOwnerApproval) // Check if the owner has approved the return
                .orElse(false); // Return false if no proof exists
    }

    public ReturnProofEntity getReturnProofByOrderId(int orderId) {
        // Use the custom method to find ReturnProof by orderId
        Optional<ReturnProofEntity> proofOptional = returnProofRepository.findByOrder_OrderId(orderId);

        // If not found, throw an exception
        if (!proofOptional.isPresent()) {
            throw new RuntimeException("Return proof not found for order ID: " + orderId);
        }

        return proofOptional.get(); // Return the ReturnProof entity
    }

}
