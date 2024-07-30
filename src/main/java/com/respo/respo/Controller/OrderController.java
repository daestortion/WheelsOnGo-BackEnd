package com.respo.respo.Controller;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.stream.Collectors;

import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Service.CarService;
import com.respo.respo.Service.OrderService;
import com.respo.respo.Service.UserService;
import org.springframework.util.StreamUtils;

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
            // Debug: Print request content type
            String contentType = request.getContentType();
            System.out.println("Request Content-Type: " + contentType);

            // Debug: Check if order is null
            if (order == null) {
                System.out.println("Order entity is null. Exiting.");
                return new ResponseEntity<>("Order entity is null.", HttpStatus.BAD_REQUEST);
            }

            // Debug: Print order details
            System.out.println("Order Details: ");
            System.out.println("Start Date: " + order.getStartDate());
            System.out.println("End Date: " + order.getEndDate());
            System.out.println("Total Price: " + order.getTotalPrice());
            System.out.println("Payment Option: " + order.getPaymentOption());
            System.out.println("Is Deleted: " + order.isDeleted());
            System.out.println("Reference Number: " + order.getReferenceNumber());

            // Retrieve and set user and car
            UserEntity user = userv.getUserById(userId);
            CarEntity car = cserv.getCarById(carId);
            order.setUser(user);
            order.setCar(car);

            // Debug: Print user and car details
            System.out.println("User Details: " + user.getUsername());
            System.out.println("Car Details: " + car.getCarModel());

            // Handle file upload if available
            if (file != null && !file.isEmpty()) {
                System.out.println("Received file with size: " + file.getSize());
                order.setPayment(file.getBytes());
            } else {
                System.out.println("No file received, payment option: Cash");
            }

            // Save order
            OrderEntity savedOrder = oserv.insertOrder(order);

            // Debug: Print saved order details
            System.out.println("Saved Order Details: ");
            System.out.println("Order ID: " + savedOrder.getOrderId());
            System.out.println("Reference Number: " + savedOrder.getReferenceNumber());

            return new ResponseEntity<>(savedOrder, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error creating order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/insertCashOrder")
    public ResponseEntity<?> insertCashOrder(@RequestParam("userId") int userId,
                                             @RequestParam("carId") int carId,
                                             @RequestBody OrderEntity order) {
        try {
            // Debug: Check if order is null
            if (order == null) {
                System.out.println("Order entity is null. Exiting.");
                return new ResponseEntity<>("Order entity is null.", HttpStatus.BAD_REQUEST);
            }

            // Debug: Print order details
            System.out.println("Order Details: ");
            System.out.println("Start Date: " + order.getStartDate());
            System.out.println("End Date: " + order.getEndDate());
            System.out.println("Total Price: " + order.getTotalPrice());
            System.out.println("Payment Option: " + order.getPaymentOption());
            System.out.println("Is Deleted: " + order.isDeleted());
            System.out.println("Reference Number: " + order.getReferenceNumber());

            // Retrieve and set user and car
            UserEntity user = userv.getUserById(userId);
            CarEntity car = cserv.getCarById(carId);
            order.setUser(user);
            order.setCar(car);

            // Debug: Print user and car details
            System.out.println("User Details: " + user.getUsername());
            System.out.println("Car Details: " + car.getCarModel());

            // Save order
            OrderEntity savedOrder = oserv.insertOrder(order);

            // Debug: Print saved order details
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
    public List<OrderEntity> getOrdersByUserId(@PathVariable int userId, @RequestParam(required = false) Boolean active) {
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
            OrderEntity updatedOrder = oserv.approveOrder(orderId);
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
}
