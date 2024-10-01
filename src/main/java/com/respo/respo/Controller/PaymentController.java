package com.respo.respo.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.respo.respo.Service.PayMongoService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PayMongoService payMongoService;

    @PostMapping("/create-link")
    public String createPaymentLink(@RequestBody Map<String, Object> payload) {
        int amount = (int) payload.get("amount");
        String description = (String) payload.get("description");

        return payMongoService.createPaymentLink(amount, description);
    }
}
