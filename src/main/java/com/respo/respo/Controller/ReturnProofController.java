package com.respo.respo.Controller;

import com.respo.respo.Entity.ReturnProofEntity;
import com.respo.respo.Service.ReturnProofService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @PostMapping
    public ReturnProofEntity createReturnProof(@RequestBody ReturnProofEntity returnProof) {
        return returnProofService.createReturnProof(returnProof);
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

    
}
