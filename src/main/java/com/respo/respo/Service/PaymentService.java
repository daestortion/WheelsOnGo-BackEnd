package com.respo.respo.Service;

import com.respo.respo.Entity.PaymentEntity;
import com.respo.respo.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    // Create a payment record
    public PaymentEntity insertPayment(PaymentEntity payment) {
        return paymentRepo.save(payment);
    }

    // Retrieve all payment records
    public List<PaymentEntity> getAllPayments() {
        return paymentRepo.findAll();
    }

    // Update a payment record
    public PaymentEntity updatePayment(PaymentEntity updatedPayment) {
        PaymentEntity payment = paymentRepo.findById(updatedPayment.getPaymentId())
                .orElseThrow(() -> new IllegalStateException("Payment not found with id: " + updatedPayment.getPaymentId()));
        payment.setProof(updatedPayment.getProof());
        payment.setOrder(updatedPayment.getOrder());
        return paymentRepo.save(payment);
    }

    // Delete a payment record
    public String deletePayment(int paymentId) {
        PaymentEntity payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("Payment not found with id: " + paymentId));
        paymentRepo.delete(payment);
        return "Payment with ID " + paymentId + " has been deleted";
    }
}
