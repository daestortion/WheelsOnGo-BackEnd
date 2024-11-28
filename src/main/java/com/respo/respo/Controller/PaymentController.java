package com.respo.respo.Controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.PaymentEntity;
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Service.PayMongoService;
import com.respo.respo.Service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

     @Autowired
    private PaymentService paymentService;

    @Autowired
    private PayMongoService payMongoService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/create")
    public ResponseEntity<PaymentEntity> createPayment(@RequestBody Map<String, Object> paymentData) {
        try {
            // Log incoming payment data
            System.out.println("Received payment data:");
            paymentData.forEach((key, value) -> System.out.println(key + ": " + value));
    
            int orderId = (Integer) paymentData.get("orderId");
            String paymentMethod = (String) paymentData.get("paymentOption");
            String transactionId = (String) paymentData.get("transactionId");
            int status = (Integer) paymentData.get("status");
    
            // Ensure the amount is treated as a float
            Object amountObj = paymentData.get("amount");
            float amount = 0f;
            if (amountObj instanceof Integer) {
                amount = ((Integer) amountObj).floatValue(); // Convert Integer to float
            } else if (amountObj instanceof Float) {
                amount = (Float) amountObj; // If it's already a Float, use it directly
            }
    
            // Fetch the order directly from the repository
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found"));
    
            // Log order details
            System.out.println("Fetched Order Details: " + order);

            // Create the payment
            PaymentEntity payment = paymentService.createPayment(order, amount, paymentMethod, null, status);
    
            // Log created payment
            System.out.println("Created Payment: " + payment);
    
            return new ResponseEntity<>(payment, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log error details
            System.err.println("Error creating payment: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    


    // Endpoint to get payments by order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentEntity>> getPaymentsByOrder(@PathVariable int orderId) {
        try {
            // Fetch the order directly from the repository
            OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found"));

            List<PaymentEntity> payments = paymentService.getPaymentsByOrder(order);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to update the status of a payment
    @PutMapping("/{paymentId}/status")
    public ResponseEntity<PaymentEntity> updatePaymentStatus(
        @PathVariable int paymentId,
        @RequestParam int status) {

        try {
            PaymentEntity updatedPayment = paymentService.updatePaymentStatus(paymentId, status);
            return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Endpoint to create a payment link
    @PostMapping("/create-link")
    public String createPaymentLink(@RequestBody Map<String, Object> payload) {
        int amount = (int) payload.get("amount");
        String description = (String) payload.get("description");

        return payMongoService.createPaymentLink(amount, description);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("Webhook received: " + payload);
            if (payload.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                String eventType = (String) data.get("type");
                
                if ("payment.paid".equals(eventType)) {
                    Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
                    int amount = (int) attributes.get("amount");
                    String externalRef = (String) attributes.get("external_reference");
                    
                    // Use external reference (order ID) to update order and payment
                    payMongoService.insertOrderAfterPayment(externalRef, amount);
                }
            }
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error handling webhook");
        }
    }
    
    // Helper method to safely extract an integer from a map
    private int extractAsInt(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Integer) {
            return (int) value;
        } else if (value instanceof String) {
            return Integer.parseInt((String) value); // Handle string numbers
        } else {
            throw new IllegalArgumentException("Unexpected value type for key: " + key);
        }
    }

    @GetMapping("/test")
public ResponseEntity<String> testEndpoint() {
    return new ResponseEntity<>("Test endpoint working", HttpStatus.OK);
}

}
