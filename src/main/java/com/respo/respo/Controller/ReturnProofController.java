package com.respo.respo.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.respo.respo.Entity.ReturnProofEntity;
import com.respo.respo.Service.ReturnProofService;

@RestController
@RequestMapping("/returnProof")
public class ReturnProofController {

    private final ReturnProofService returnProofService;

    @Autowired
    public ReturnProofController(ReturnProofService returnProofService) {
        this.returnProofService = returnProofService;
    }

    @GetMapping
    public List<ReturnProofEntity> getAllReturnProofs() {
        return returnProofService.getAllReturnProofs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnProofEntity> getReturnProofById(@PathVariable int id) {
        Optional<ReturnProofEntity> returnProof = returnProofService.getReturnProofById(id);
        return returnProof.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/createReturnProof")
    public ReturnProofEntity createReturnProof(@RequestParam("proof") MultipartFile proof, 
                                                @RequestParam("remarks") String remarks,
                                                @RequestParam("returnDate") String returnDate, 
                                                @RequestParam("orderId") int orderId) {
        try {
            ReturnProofEntity returnProof = new ReturnProofEntity();
            returnProof.setProof(proof.getBytes()); // Convert MultipartFile to byte array
            returnProof.setRemarks(remarks);
            returnProof.setReturnDate(LocalDate.parse(returnDate));

            return returnProofService.createReturnProof(returnProof, orderId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process proof file", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReturnProofEntity> updateReturnProof(@PathVariable int id, @RequestBody ReturnProofEntity updatedReturnProof) {
        try {
            ReturnProofEntity updated = returnProofService.updateReturnProof(id, updatedReturnProof);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturnProof(@PathVariable int id) {
        returnProofService.deleteReturnProof(id);
        return ResponseEntity.noContent().build();
    }

    // New: Update owner details for the return
    @PutMapping("/updateReturnProof/{orderId}")
    public ResponseEntity<?> updateOwnerReturnProof(
            @PathVariable int orderId,
            @RequestParam("ownerProof") MultipartFile ownerProof,
            @RequestParam("ownerRemark") String ownerRemark,
            @RequestParam("ownerApproval") boolean ownerApproval) {
        try {
            ReturnProofEntity updatedProof = returnProofService.updateOwnerProof(
                    orderId, ownerProof, ownerRemark, ownerApproval);
            return ResponseEntity.ok(updatedProof);
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/getReturnDetails/{orderId}")
    public ResponseEntity<?> getReturnDetails(@PathVariable int orderId) {
        try {
            Map<String, Object> renterDetails = returnProofService.getRenterDetails(orderId);
            return ResponseEntity.ok(renterDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Return proof not found for order ID: " + orderId);
        }
    }

}
