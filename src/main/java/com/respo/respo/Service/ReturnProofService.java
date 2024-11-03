package com.respo.respo.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            if (daysLate > 1) {
                returnProof.setPenalty(500); // Set penalty if return is more than 1 day late
            } else {
                returnProof.setPenalty(0); // No penalty if return is on time or within 1 day
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
}
