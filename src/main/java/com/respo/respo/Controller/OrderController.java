package com.respo.respo.Controller;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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
    public OrderEntity insertOrder(@RequestPart("order") OrderEntity order,
                                   @RequestParam("userId") int userId,
                                   @RequestParam("carId") int carId,
                                   @RequestPart("file") MultipartFile file) throws IOException {
        UserEntity user = userv.getUserById(userId);
        CarEntity car = cserv.getCarById(carId);
        order.setUser(user);
        order.setCar(car);
        
        if (file != null && !file.isEmpty()) {
            System.out.println("Received file with size: " + file.getSize());
            order.setPayment(file.getBytes());
        } else {
            System.out.println("No file received");
        }

        return oserv.insertOrder(order);
    }
	
	//Read
	@GetMapping("/getAllOrders")
	public List<OrderEntity> getAllOrders() {
		return oserv.getAllOrders();
	}
	
	//U - Update a order record
	@PutMapping("/updateOrder")
	public OrderEntity updateOrder(@RequestParam int orderId, @RequestBody OrderEntity newOrderDetails) {
		return oserv.updateOrder(orderId, newOrderDetails);
	}
				
	//D - Delete a order record
	@DeleteMapping("/deleteOrder/{orderId}")
	public String deleteUser(@PathVariable int orderId) {
		return oserv.deleteOrder(orderId);
	}

	@GetMapping("/getOrdersByUserId/{userId}")
    public List<OrderEntity> getOrdersByUserId(@PathVariable int userId) {
        UserEntity user = userv.getUserById(userId);
        return oserv.getOrdersByUserId(user);
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
}
