package com.respo.respo.Service;

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
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    
        returnProof.setOrder(order);
        returnProof.setEndDate(order.getEndDate()); // Set endDate from OrderEntity to ReturnProofEntity
    
        // Calculate the penalty based on return date and order end date
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

    // New: Fetch renter-submitted details
    public Map<String, Object> getRenterDetails(int orderId) {
        ReturnProofEntity proof = returnProofRepository.findById(orderId)
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
    public ReturnProofEntity updateOwnerProof(int orderId, MultipartFile ownerProof, 
                                              String ownerRemark, boolean ownerApproval) throws IOException {
        ReturnProofEntity proof = returnProofRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Return proof not found for order ID: " + orderId));

        proof.setOwnerProof(ownerProof.getBytes());
        proof.setOwnerRemark(ownerRemark);
        proof.setOwnerApproval(ownerApproval);

        return returnProofRepository.save(proof);
    }

}
