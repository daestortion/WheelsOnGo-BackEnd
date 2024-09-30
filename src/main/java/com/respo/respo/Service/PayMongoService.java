package com.respo.respo.Service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayMongoService {

    private static final String PAYMONGO_URL = "https://api.paymongo.com/v1/links";
    private static final String API_KEY = "sk_test_3mN2xCjzWs14ur254hi39QmF";  // Replace with your secret key

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
}