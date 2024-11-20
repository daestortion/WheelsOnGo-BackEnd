package com.respo.respo.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.PaymentEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Repository.PaymentRepository;

@Service
public class PayMongoService {

    private static final String PAYMONGO_URL = "https://api.paymongo.com/v1/links";
    private static final String API_KEY = "sk_test_TJm895xJBi9VSxyJMG2a9Sue";

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private CarService carService;

    @Autowired
    private OrderService orderService;

    public String createPaymentLink(int amount, String description) {
        RestTemplate restTemplate = new RestTemplate();
    
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = API_KEY + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
    
        // Set request body
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("amount", amount); // Amount in centavos (e.g. 10000 = PHP 100.00)
        attributes.put("currency", "PHP");
        attributes.put("description", description);
    
        Map<String, Object> data = new HashMap<>();
        data.put("attributes", attributes);
    
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", data);
    
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
    
        // Send request to PayMongo
        ResponseEntity<String> response = restTemplate.postForEntity(PAYMONGO_URL, request, String.class);
    
        return response.getBody();  // Handle the response as needed
    }
    

    // New method to handle order creation after payment confirmation
    public void insertOrderAfterPayment(String externalReference, int amount) {
        // Retrieve the order using the external reference (e.g., referenceNumber)
        OrderEntity order = orderService.getOrderByReferenceNumber(externalReference);
    
        // Mark the order as paid
        order.setStatus(1); // Paid
        order.setActive(true);
    
        // Create a payment record
        PaymentEntity payment = new PaymentEntity();
        payment.setOrder(order);
        payment.setAmount(amount / 100.0f); // Convert centavos to PHP
        payment.setPaymentMethod("PayMongo");
        payment.setStatus(1); // Successful
    
        // Save updates
        orderRepo.save(order);
        paymentRepo.save(payment);
    }
    
}