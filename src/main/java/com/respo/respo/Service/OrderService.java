package com.respo.respo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Repository.CarRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class OrderService {

	@Autowired
	OrderRepository orepo;
	CarRepository crepo;

	// Create
	// Create
	// Create
	public OrderEntity insertOrder(OrderEntity order) {
	    // Check if the reference number is already set and is not empty, generate if necessary
	    if (order.getReferenceNumber() == null || order.getReferenceNumber().isEmpty()) {
	        String newReferenceNumber = order.generateReferenceNumber(); // Generate a new reference number
	        order.setReferenceNumber(newReferenceNumber); // Set the newly generated reference number
	    }

	    // Set user as renting
	    UserEntity user = order.getUser();
	    if (user != null) {
	        user.setRenting(true); // Set the user's isRenting status to true
	        // Persist changes to the user entity if necessary, e.g., userRepository.save(user);
	    }

	    // Set car as rented
	    CarEntity car = order.getCar();
	    if (car != null) {
	        car.setRented(true); // Set the car's isRented status to true
	        car.addOrder(order); // Add the order to the car's list of orders
	        // Persist changes to the car entity if necessary, e.g., carRepository.save(car);
	    }
	    // Save the order with the reference number and updated entity statuses
	    return orepo.save(order);
	}


	// Read
	public List<OrderEntity> getAllOrders() {
		return orepo.findAll();
	}

	// U - update
	@SuppressWarnings("finally")
	public OrderEntity updateOrder(int orderId, OrderEntity newOrderDetails) {
		OrderEntity order = new OrderEntity();
		try {
			// search the id number of the order that will be updated
			order = orepo.findById(orderId).get();

			// update the record
			// order.set(newOrderDetails.get());

		} catch (NoSuchElementException ex) {
			throw new NoSuchElementException("Order " + orderId + " does not exist!");
		} finally {
			return orepo.save(order);
		}
	}

	// D - delete
	public String deleteOrder(int orderId) {
		OrderEntity order = orepo.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order " + orderId + "does not exist"));

		if (order.isDeleted()) {
			return "Order #" + orderId + " is already deleted!";
		} else {
			order.setDeleted(true);
			orepo.save(order);
			return "Order #" + orderId + "has been deleted";
		}
	}

	    // Get orders by user ID
    public List<OrderEntity> getOrdersByUserId(UserEntity user) {
        return orepo.findByUser(user);
    }

	public OrderEntity approveOrder(int orderId) {
		OrderEntity order = orepo.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order " + orderId + " does not exist"));
	
		order.setStatus(1); // Assuming status is a boolean field, set it to true
		return orepo.save(order);
	}

	public OrderEntity denyOrder(int orderId) {
        OrderEntity order = orepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order " + orderId + " does not exist"));

        order.setStatus(2); // Set order status to 2

        // Set user renting status to false
        UserEntity user = order.getUser();
        if (user != null) {
            user.setRenting(false);
        }

        // Set car rented status to false
        CarEntity car = order.getCar();
        if (car != null) {
            car.setRented(false);
        }

        return orepo.save(order);
    }

    public List<OrderEntity> getOrdersByCarOwnerId(int ownerId) {
        List<CarEntity> cars = crepo.findByOwnerId(ownerId);
        return cars.stream()
                   .flatMap(car -> orepo.findByCar(car).stream())
                   .collect(Collectors.toList());
    }
}