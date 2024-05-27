package com.respo.respo.Controller;

import com.respo.respo.Entity.PaymentEntity;
import com.respo.respo.Service.PaymentService;

import java.util.List; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = ("https://main--wheelsongo.netlify.app"))
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Create a payment
    @PostMapping("/create")
    public ResponseEntity<PaymentEntity> createPayment(@RequestBody PaymentEntity payment) {
        return ResponseEntity.ok(paymentService.insertPayment(payment));
    }

    // Read all payments
    @GetMapping("/getAll")
    public ResponseEntity<List<PaymentEntity>> getAllPayments() {
        List<PaymentEntity> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // Update a payment
    @PutMapping("/update")
    public ResponseEntity<PaymentEntity> updatePayment(@RequestBody PaymentEntity payment) {
        return ResponseEntity.ok(paymentService.updatePayment(payment));
    }

    // Delete a payment
    @DeleteMapping("/delete/{paymentId}")
    public ResponseEntity<String> deletePayment(@PathVariable int paymentId) {
        return ResponseEntity.ok(paymentService.deletePayment(paymentId));
    }
}
