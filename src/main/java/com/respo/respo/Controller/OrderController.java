package com.respo.respo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    
 // Create
    @PostMapping("/insertOrder")
    public OrderEntity insertOrder(@RequestBody OrderEntity order, @RequestParam int userId, @RequestParam int carId) {
        UserEntity user = userv.getUserById(userId); // Fetch the user by ID
        CarEntity car = cserv.getCarById(carId); // Fetch the car by ID

        order.setUser(user);
        order.setCar(car);

        return oserv.insertOrder(order); // Call the insertOrder method to save the order properly
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
}
