package com.respo.respo.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;

import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.PaymentEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Service.CarService;
import com.respo.respo.Service.OrderService;
import com.respo.respo.Service.PaymentService;
import com.respo.respo.Service.UserService;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "http://main--wheelsongo.netlify.app", allowedHeaders = "*", allowCredentials = "true")
public class OrderController {

    @Autowired
    private OrderService oserv;

    @Autowired
    private UserService userv;

    @Autowired
    private CarService cserv;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/insertOrder")
    public ResponseEntity<?> insertOrder(
            @RequestParam("userId") int userId,
            @RequestParam("carId") int carId,
            @RequestBody OrderEntity order) { // Remove file part
    
        try {
            // Insert the order and related payment (cash) into the database
            OrderEntity savedOrder = oserv.insertOrder(userId, carId, order);
            return new ResponseEntity<>(savedOrder, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error creating order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    

    @PutMapping("/updatePaymentStatus")
    public ResponseEntity<String> updatePaymentStatus(@RequestBody Map<String, Object> paymentData) {
        try {
            if (!paymentData.containsKey("orderId") || !paymentData.containsKey("transactionId")) {
                throw new IllegalArgumentException("Missing 'orderId' or 'transactionId' in payment data.");
            }
    
            oserv.updatePaymentStatus(paymentData);  // Pass the data to the service layer
            return new ResponseEntity<>("Payment status updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error updating payment status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    


    @GetMapping("/getProofOfPayment/{orderId}")
    public ResponseEntity<byte[]> getProofOfPayment(@PathVariable int orderId) {
        try {
            // Retrieve the order and associated payments
            OrderEntity order = oserv.getOrderById(orderId);
            List<PaymentEntity> payments = paymentService.getPaymentsByOrder(order);

            if (payments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Assume the latest or a specific payment is desired, e.g., the first one
            PaymentEntity payment = payments.get(0); // You could select based on other criteria if needed
            byte[] proofOfPayment = payment.getProofOfPayment();

            if (proofOfPayment != null) {
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(proofOfPayment);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getAllOrders")
    public List<OrderEntity> getAllOrders() {
        return oserv.getAllOrders();
    }

    @PutMapping("/updateOrder")
    public OrderEntity updateOrder(@RequestParam int orderId, @RequestBody OrderEntity newOrderDetails) {
        return oserv.updateOrder(orderId, newOrderDetails);
    }

    @DeleteMapping("/deleteOrder/{orderId}")
    public String deleteUser(@PathVariable int orderId) {
        return oserv.deleteOrder(orderId);
    }

    @GetMapping("/getOrdersByUserId/{userId}")
    public List<OrderEntity> getOrdersByUserId(@PathVariable int userId,
            @RequestParam(required = false) Boolean active) {
        UserEntity user = userv.getUserById(userId);
        List<OrderEntity> orders = oserv.getOrdersByUserId(user);
        if (active != null && active) {
            orders = orders.stream().filter(OrderEntity::isActive).collect(Collectors.toList());
        }
        return orders;
    }

    @GetMapping("/getOrdersByCarOwnerId/{ownerId}")
    public ResponseEntity<List<OrderEntity>> getOrdersByCarOwnerId(@PathVariable int ownerId) {
        try {
            List<OrderEntity> orders = oserv.getOrdersByCarOwnerId(ownerId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getOrdersByCarId/{carId}")
    public ResponseEntity<List<OrderEntity>> getOrdersByCarId(@PathVariable int carId) {
        try {
            // Use CarService to get the car entity by carId
            CarEntity car = cserv.getCarById(carId);

            // Use OrderRepository to find orders by car
            List<OrderEntity> orders = oserv.getOrdersByCar(car);

            // Return the list of orders
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/extendOrder/{orderId}")
    public ResponseEntity<OrderEntity> extendOrder(@PathVariable int orderId,
                                                @RequestParam("newEndDate") String newEndDateStr) {
        try {
            LocalDate newEndDate = LocalDate.parse(newEndDateStr);
            OrderEntity newOrder = oserv.extendOrder(orderId, newEndDate);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);  // Use CREATED status for new order
        } catch (NoSuchElementException | IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/markAsReturned/{orderId}")
    public ResponseEntity<String> markAsReturned(@PathVariable int orderId) {
        try {
            // Call the service to mark the order as returned and log the activity
            oserv.markAsReturned(orderId);

            return new ResponseEntity<>("Order marked as returned successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error marking order as returned: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getOrderById/{orderId}")
    public ResponseEntity<OrderEntity> getOrderById(@PathVariable int orderId) {
        try {
            OrderEntity order = oserv.getOrderById(orderId); // Fetch the order by its ID
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/terminateOrder/{orderId}")
    public ResponseEntity<OrderEntity> terminateOrder(@PathVariable int orderId) {
        try {
            // Call the service to terminate the order
            OrderEntity terminatedOrder = oserv.terminateOrder(orderId);
            return new ResponseEntity<>(terminatedOrder, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Catch any other potential errors and return a bad request
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/approveOrder/{orderId}")
    public ResponseEntity<OrderEntity> approveOrder(@PathVariable int orderId) {
        try {
            // Approve the order and set the paid status within the service layer
            OrderEntity approvedOrder = oserv.approveOrder(orderId);
            return new ResponseEntity<>(approvedOrder, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/checkExistingPayment")
    public ResponseEntity<Map<String, Boolean>> checkExistingPayment(@RequestParam("transactionId") String transactionId) {
        boolean exists = paymentService.checkExistingPayment(transactionId);
        Map<String, Boolean> response = Collections.singletonMap("exists", exists);
        return ResponseEntity.ok(response);
    }
    
}
