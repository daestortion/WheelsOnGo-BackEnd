package com.respo.respo.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.PaymentEntity;
import com.respo.respo.Repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    public PaymentEntity createPayment(OrderEntity order, float amount, String paymentMethod, byte[] proofOfPayment, int status) {
        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(status); // Set status to pending (0 for cash payments)
        return paymentRepo.save(payment);
    }
    

    public List<PaymentEntity> getPaymentsByOrder(OrderEntity order) {
        return paymentRepo.findByOrder(order);
    }

    public PaymentEntity updatePaymentStatus(int paymentId, int status) {
        PaymentEntity payment = paymentRepo.findById(paymentId)
            .orElseThrow(() -> new NoSuchElementException("Payment with ID " + paymentId + " not found."));
        payment.setStatus(status);
        return paymentRepo.save(payment);
    }
}
