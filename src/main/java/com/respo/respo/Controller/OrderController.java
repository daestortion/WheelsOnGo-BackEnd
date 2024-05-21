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

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Service.OrderService;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = ("http://localhost:3000"))
public class OrderController {

	@Autowired
	OrderService oserv;
	
	@GetMapping("/print")
	public String itWorks() {
		return "It works";
	}
	
	//Create
	@PostMapping("/insertOrder")
	public OrderEntity insertUser(@RequestBody OrderEntity order) {
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
}
