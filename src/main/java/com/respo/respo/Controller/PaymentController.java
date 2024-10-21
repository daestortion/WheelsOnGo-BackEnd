package com.respo.respo.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.respo.respo.Service.PayMongoService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PayMongoService payMongoService;

    // Endpoint to create a payment link
    @PostMapping("/create-link")
    public String createPaymentLink(@RequestBody Map<String, Object> payload) {
        int amount = (int) payload.get("amount");
        String description = (String) payload.get("description");

        return payMongoService.createPaymentLink(amount, description);
    }

    // Webhook handler to confirm the payment
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // Log the incoming webhook payload
            System.out.println("Webhook received: " + payload);

            // Extract event type directly from the payload
            String eventType = (String) payload.get("type");

            // Process only the payment.paid event
            if ("payment.paid".equals(eventType)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");

                // Safely extract necessary details from the webhook payload
                int userId = extractAsInt(attributes, "userId");
                int carId = extractAsInt(attributes, "carId");
                int amount = extractAsInt(attributes, "amount");

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
