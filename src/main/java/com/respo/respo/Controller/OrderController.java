package com.respo.respo.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Service.CarService;
import com.respo.respo.Service.OrderService;
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

    @PostMapping("/insertOrder")
    public ResponseEntity<?> insertOrder(@RequestParam("userId") int userId,
                                        @RequestParam("carId") int carId,
                                        @RequestPart(value = "order", required = false) OrderEntity order,
                                        @RequestPart(value = "file", required = false) MultipartFile file,
                                        HttpServletRequest request) {
        try {
            if (order == null) {
                return new ResponseEntity<>("Order entity is null.", HttpStatus.BAD_REQUEST);
            }

            // Fetch user and car entities
            UserEntity user = userv.getUserById(userId);
            CarEntity car = cserv.getCarById(carId);
            order.setUser(user);
            order.setCar(car);

            // Process payment logic based on the provided payment option
            if ("Paymongo".equalsIgnoreCase(order.getPaymentOption())) {
                order.setStatus(1); // Status 1 for paid orders
                order.setPaid(true); // Mark as paid
            } else if (file != null && !file.isEmpty()) {
                order.setPayment(file.getBytes()); // Save proof of payment file
                order.setStatus(0); // Status 0 for pending orders
            } else {
                order.setStatus(0); // Status 0 for pending orders if no file
            }

            // Set the user renting status and car rented status
            user.setRenting(true);
            car.setRented(true);

            // Insert the order
            OrderEntity savedOrder = oserv.insertOrder(order);

            // Log order activity only once
            oserv.logOrderActivity(savedOrder);

            return new ResponseEntity<>(savedOrder, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error creating order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/updatePaymentStatus")
    public ResponseEntity<String> updatePaymentStatus(@RequestBody Map<String, Object> paymentData) {
        try {
            // Extract order ID and transaction details from the request body
            int orderId = (Integer) paymentData.get("orderId");
            String transactionId = (String) paymentData.get("transactionId");

            // Retrieve the order by its ID
            OrderEntity order = oserv.getOrderById(orderId);

            // Update payment details in the order entity
            order.setStatus(1);  // Set the order status as paid
            order.setReferenceNumber(transactionId);  // Use transaction ID from PayPal
            order.setPaymentOption("PayPal");  // Set payment method as PayPal
            
            // Automatically set paid to true if payment option is PayPal
            if ("PayPal".equalsIgnoreCase(order.getPaymentOption())) {
                order.setPaid(true);  // Mark order as paid
            }

            // Save the updated order back to the database
            oserv.insertOrder(order);

            return new ResponseEntity<>("Payment status updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error updating payment status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/insertCashOrder")
    public ResponseEntity<?> insertCashOrder(@RequestParam("userId") int userId,
            @RequestParam("carId") int carId,
            @RequestBody OrderEntity order) {
        try {
            if (order == null) {
                System.out.println("Order entity is null. Exiting.");
                return new ResponseEntity<>("Order entity is null.", HttpStatus.BAD_REQUEST);
            }

            System.out.println("Order Details: ");
            System.out.println("Start Date: " + order.getStartDate());
            System.out.println("End Date: " + order.getEndDate());
            System.out.println("Total Price: " + order.getTotalPrice());
            System.out.println("Payment Option: " + order.getPaymentOption());
            System.out.println("Is Deleted: " + order.isDeleted());
            System.out.println("Reference Number: " + order.getReferenceNumber());

            UserEntity user = userv.getUserById(userId);
            CarEntity car = cserv.getCarById(carId);
            order.setUser(user);
            order.setCar(car);

            System.out.println("User Details: " + user.getUsername());
            System.out.println("Car Details: " + car.getCarModel());

            order.setStatus(0); // Assuming 0 is the status for pending orders
            user.setRenting(true); // Set the user's renting status to true

            OrderEntity savedOrder = oserv.insertOrder(order);

            System.out.println("Saved Order Details: ");
            System.out.println("Order ID: " + savedOrder.getOrderId());
            System.out.println("Reference Number: " + savedOrder.getReferenceNumber());

            return new ResponseEntity<>(savedOrder, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error creating order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getProofOfPayment/{orderId}")
    public void getProofOfPayment(@PathVariable int orderId, HttpServletResponse response) {
        try {
            OrderEntity order = oserv.getOrderById(orderId);
            byte[] imageBytes = order.getPayment();

            if (imageBytes != null) {
                response.setContentType("image/jpeg");
                StreamUtils.copy(imageBytes, response.getOutputStream());
            } else {
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (NoSuchElementException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
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

    @PutMapping("/approveOrder/{orderId}")
    public ResponseEntity<OrderEntity> approveOrder(@PathVariable int orderId) {
        try {
            // Fetch the order by its ID
            OrderEntity order = oserv.getOrderById(orderId);
            
            // Set the order status to approved
            order.setStatus(1); // Assuming 1 represents 'approved'
            
            // Set the 'paid' status to true when the order is approved
            order.setPaid(true);
            
            // Save the updated order
            OrderEntity updatedOrder = oserv.insertOrder(order);
            
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/denyOrder/{orderId}")
    public ResponseEntity<OrderEntity> denyOrder(@PathVariable int orderId) {
        try {
            OrderEntity updatedOrder = oserv.denyOrder(orderId);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
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

    @PutMapping("/extendOrder/{orderId}")
    public ResponseEntity<OrderEntity> extendOrder(@PathVariable int orderId,
            @RequestParam("newEndDate") String newEndDateStr) {
        try {
            LocalDate newEndDate = LocalDate.parse(newEndDateStr);
            OrderEntity updatedOrder = oserv.extendOrder(orderId, newEndDate);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
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
            return new ResponseEntity<>("Error marking order as returned: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getOrderById/{orderId}")
    public ResponseEntity<OrderEntity> getOrderById(@PathVariable int orderId) {
        try {
            OrderEntity order = oserv.getOrderById(orderId);  // Fetch the order by its ID
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

}
