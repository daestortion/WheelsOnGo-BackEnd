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

    // Endpoint to create a payment
    @PostMapping("/create")
    public ResponseEntity<PaymentEntity> createPayment(
        @RequestParam int orderId,
        @RequestParam float amount,
        @RequestParam String paymentMethod,
        @RequestParam(required = false) byte[] proofOfPayment,
        @RequestParam int status) {

        try {
            // Fetch the order directly from the repository
            OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order with ID " + orderId + " not found"));

            PaymentEntity payment = paymentService.createPayment(order, amount, paymentMethod, proofOfPayment, status);
            return new ResponseEntity<>(payment, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
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
        // Log the incoming webhook payload
        System.out.println("Webhook received: " + payload);

        // Check if 'data' exists in the payload
        if (!payload.containsKey("data")) {
            System.out.println("Error: 'data' object not found in the payload");
            return new ResponseEntity<>("Missing 'data' object", HttpStatus.BAD_REQUEST);
        }

        // Access the 'data' object first
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        System.out.println("Data object: " + data);

        // Check if 'type' exists in the 'data' object
        if (!data.containsKey("type")) {
            System.out.println("Error: 'type' not found in 'data' object");
            return new ResponseEntity<>("Missing 'type' in 'data'", HttpStatus.BAD_REQUEST);
        }

        // Extract the 'type' from the 'data' object
        String eventType = (String) data.get("type");
        System.out.println("Event Type: " + eventType);

        if ("payment.paid".equals(eventType)) {
            // Check if 'attributes' exists in 'data' object
            if (!data.containsKey("attributes")) {
                System.out.println("Error: 'attributes' object not found in 'data'");
                return new ResponseEntity<>("Missing 'attributes' in 'data'", HttpStatus.BAD_REQUEST);
            }

            Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
            System.out.println("Attributes: " + attributes);

            // Extract necessary details from the attributes
            int userId = extractAsInt(attributes, "userId");
            int carId = extractAsInt(attributes, "carId");
            int amount = extractAsInt(attributes, "amount");

            System.out.println("User ID: " + userId + ", Car ID: " + carId + ", Amount: " + amount);

            // Call the service method to insert the order after payment confirmation
            payMongoService.insertOrderAfterPayment(userId, carId, amount);

            return new ResponseEntity<>("Webhook processed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unhandled event type", HttpStatus.BAD_REQUEST);
        }
    } catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>("Error processing webhook: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
