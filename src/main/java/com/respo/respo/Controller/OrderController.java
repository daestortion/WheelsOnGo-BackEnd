package com.respo.respo.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
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
            String contentType = request.getContentType();
            System.out.println("Request Content-Type: " + contentType);

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

            if (file != null && !file.isEmpty()) {
                System.out.println("Received file with size: " + file.getSize());
                order.setPayment(file.getBytes());
            } else {
                System.out.println("No file received, payment option: Cash");
            }

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
}
