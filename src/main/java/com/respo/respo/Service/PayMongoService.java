package com.respo.respo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Service.UserService;
import com.respo.respo.Service.CarService;
import com.respo.respo.Service.OrderService;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayMongoService {

    private static final String PAYMONGO_URL = "https://api.paymongo.com/v1/links";
    private static final String API_KEY = "sk_test_3mN2xCjzWs14ur254hi39QmF";  // Replace with your secret key

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

        return response.getBody();  // You can handle the response as needed
    }

    // New method to handle order creation after payment confirmation
    public void insertOrderAfterPayment(int userId, int carId, int amount) {
        // Fetch the user and car entities (you can adjust this logic as needed)
        UserEntity user = userService.getUserById(userId);
        CarEntity car = carService.getCarById(carId);
    
        // Log the details before order insertion
        System.out.println("Inserting order for User ID: " + userId + ", Car ID: " + carId + ", Amount: " + amount);
    
        // Create the order entity and set the status as paid
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setCar(car);
        order.setTotalPrice((float) amount / 100); // Cast amount to float before dividing
        order.setPaymentOption("Paymongo");
        order.setStatus(1); // 1 for paid status
        order.setPaid(true); // Mark as paid
    
        // Insert the order into the database and log the result
        orderService.insertOrder(order);
        System.out.println("Order inserted successfully: " + order);
    }
    
}