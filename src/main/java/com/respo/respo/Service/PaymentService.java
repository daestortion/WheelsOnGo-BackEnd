package com.respo.respo.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.PaymentEntity;
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private OrderRepository orderRepo;

    public PaymentEntity createPayment(OrderEntity order, float amount, String paymentMethod, byte[] proofOfPayment, int status) {
        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(status); // Set the initial payment status (can be pending)
    
        // If the payment method is PayPal or PayMongo, set the payment status to '1' (successful) and order status to '1' (paid)
        if ("PayPal".equalsIgnoreCase(paymentMethod) || "PayMongo".equalsIgnoreCase(paymentMethod)) {
            payment.setStatus(1);  // 1 indicates successful payment
            order.setStatus(1);    // 1 indicates the order is active and paid
        } 
        // If the payment method is Cash, set both payment and order status to '0' (pending)
        else if ("Cash".equalsIgnoreCase(paymentMethod)) {
            payment.setStatus(0);  // 0 indicates pending payment
            order.setStatus(0);    // 0 indicates pending order status (waiting for payment confirmation)
        }
    
        // Save the payment and update the order status
        paymentRepo.save(payment);
        orderRepo.save(order);
    
        return payment;
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
