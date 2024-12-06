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
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Repository.ReturnProofRepository;

@Service
public class ReturnProofService {

       private final ReturnProofRepository returnProofRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public ReturnProofService(ReturnProofRepository returnProofRepository, OrderRepository orderRepository) {
        this.returnProofRepository = returnProofRepository;
        this.orderRepository = orderRepository;
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
        order.setActive(false);  // Mark the order as inactive
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
        return returnProofRepository.findById(id)
            .map(existingProof -> {
                existingProof.setProof(updatedReturnProof.getProof());
                existingProof.setRemarks(updatedReturnProof.getRemarks());
                existingProof.setReturnDate(updatedReturnProof.getReturnDate());
                existingProof.setEndDate(updatedReturnProof.getEndDate());
                existingProof.setPenalty(updatedReturnProof.getPenalty());
                return returnProofRepository.save(existingProof);
            })
            .orElseThrow(() -> new RuntimeException("ReturnProof not found"));
    }

    public void deleteReturnProof(int id) {
        returnProofRepository.deleteById(id);
    }

    public Map<String, Object> getRenterDetails(int orderId) {
        // Use the custom method to find ReturnProof by orderId
        ReturnProofEntity proof = returnProofRepository.findByOrder_OrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Return proof not found for order ID: " + orderId));
        
        Map<String, Object> response = new HashMap<>();
        response.put("carOwner", proof.getOrder().getCar().getOwner().getfName() + " " + proof.getOrder().getCar().getOwner().getlName());
        response.put("renter", proof.getOrder().getUser().getfName() + " " + proof.getOrder().getUser().getlName());
        response.put("rentStartDate", proof.getOrder().getStartDate());
        response.put("rentEndDate", proof.getOrder().getEndDate());
        response.put("carReturnDate", proof.getReturnDate());
        response.put("remarks", proof.getRemarks());
        response.put("proof", Base64.getEncoder().encodeToString(proof.getProof())); // Convert the byte array to Base64
        return response;
    }  

    // New: Update owner-side return proof details
    public ReturnProofEntity updateOwnerProofAndOrder(int orderId, MultipartFile ownerProof, 
                                                    String ownerRemark, boolean ownerApproval) 
                                                    throws IOException {
        // Fetch the return proof and order
        ReturnProofEntity proof = returnProofRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Return proof not found for order ID: " + orderId));
        OrderEntity order = proof.getOrder();

        // Update acknowledgment fields
        proof.setOwnerProof(ownerProof.getBytes());
        proof.setOwnerRemark(ownerRemark);
        proof.setOwnerApproval(ownerApproval);

        // If the acknowledgment is approved, update the order
        if (ownerApproval) {
            order.setReturned(true);
            order.setReturnDate(LocalDate.now());
            orderRepository.save(order); // Save updated order
        }

        // Save the updated return proof
        return returnProofRepository.save(proof);
    }


    public boolean returnProofExists(int orderId) {
        return returnProofRepository.findByOrder_OrderId(orderId).isPresent();
    }
    
    public boolean isOwnerAcknowledged(int orderId) {
        return returnProofRepository.findByOrder_OrderId(orderId)
                .map(ReturnProofEntity::isOwnerApproval) // Check if the owner has approved the return
                .orElse(false); // Return false if no proof exists
    }
    
}
