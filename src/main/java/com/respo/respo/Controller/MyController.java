package com.respo.respo.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MyController {
    
    @GetMapping("/getEndpoint")
    public ResponseEntity<String> getMethod() {
        return ResponseEntity.ok("GET Response");
    }
    
    @PostMapping("/postEndpoint")
    public ResponseEntity<String> postMethod() {
        return ResponseEntity.ok("POST Response");
    }
    
    @PutMapping("/putEndpoint")
    public ResponseEntity<String> putMethod() {
        return ResponseEntity.ok("PUT Response");
    }
    
    @DeleteMapping("/deleteEndpoint")
    public ResponseEntity<String> deleteMethod() {
        return ResponseEntity.ok("DELETE Response");
    }
}
