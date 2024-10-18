package com.respo.respo.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.time.LocalDateTime;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.ActivityLogEntity;
import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Repository.CarRepository;
import com.respo.respo.Repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	OrderRepository orepo;
	CarRepository crepo;

	@Autowired
    ActivityLogService logService;

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

	public void logOrderActivity(OrderEntity order) {
        CarEntity car = order.getCar();
        UserEntity user = order.getUser();

        String logMessage = car.getCarModel() + " " + car.getCarBrand() + " has been rented by " +
                            user.getUsername() + " from " + order.getStartDate() + " to " + order.getEndDate();

        // Avoid duplicate logs by checking if the log already exists
        List<ActivityLogEntity> existingLogs = logService.getLogsByAction("has been rented by " + user.getUsername());
        boolean logExists = existingLogs.stream().anyMatch(log -> log.getAction().contains(car.getCarModel()));

        if (!logExists) {
            logService.logActivity(logMessage, user.getUsername());
        }
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

	public OrderEntity getOrderById(int orderId) {
		return orepo.findById(orderId).orElseThrow(() -> new NoSuchElementException("Order " + orderId + " does not exist"));
	}

	public List<OrderEntity> getOrdersByCar(CarEntity car) {
		return orepo.findByCar(car);
	}
	
	public OrderEntity extendOrder(int orderId, LocalDate newEndDate) {
		// Find the order by its ID
		OrderEntity order = orepo.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order " + orderId + " does not exist"));
	
		// Get the current end date of the order
		LocalDate currentEndDate = order.getEndDate();
		
		// Check if the new end date is after the current end date
		if (newEndDate.isBefore(currentEndDate)) {
			throw new IllegalArgumentException("New end date must be after the current end date");
		}
	
		// Calculate the additional days
		long additionalDays = currentEndDate.until(newEndDate).getDays();
		
		// Get the car associated with the order
		CarEntity car = order.getCar();
		float dailyRate = car.getRentPrice();
	
		// Recalculate the total price (adding additional days' rent to the current total)
		float newTotalPrice = order.getTotalPrice() + (dailyRate * additionalDays);
	
		// Update the order's end date and total price
		order.setEndDate(newEndDate);
		order.setTotalPrice(newTotalPrice);
	
		// Save the updated order
		OrderEntity updatedOrder = orepo.save(order);
	
		// Log the order extension
		String logMessage = "Order " + order.getOrderId() + " has been extended from " +
							currentEndDate + " to " + newEndDate + ". Days: " + additionalDays;
		logService.logActivity(logMessage, order.getUser().getUsername());
	
		return updatedOrder;
	}	
	
	 // Method to update the delivery address of an order
	 public OrderEntity updateDeliveryAddress(int orderId, String newAddress) {
        // Fetch the order by ID
        OrderEntity order = orepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order " + orderId + " not found"));

        // Set the new delivery address
        order.setDeliveryAddress(newAddress);

        // Save and return the updated order
        return orepo.save(order);
    }

	public OrderEntity terminateOrder(int orderId) {
		OrderEntity order = orepo.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order " + orderId + " does not exist"));
	
		// Set order as terminated and inactive, and capture the current date in the Philippines timezone
		order.setTerminated(true);
		order.setActive(false);  // Set the order as inactive
		ZonedDateTime philippinesTime = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
		order.setTerminationDate(philippinesTime.toLocalDate());  // Store only the date part
	
		// Optionally, set the user's and car's status to non-active
		CarEntity car = order.getCar();
		if (car != null) {
			car.setRented(false);  // Set the car as not rented
		}
	
		UserEntity user = order.getUser();
		if (user != null) {
			user.setRenting(false);  // Set the user as not renting
		}
	
		// Save the updated order
		OrderEntity terminatedOrder = orepo.save(order);
	
		// Log the order termination activity
		String logMessage = user.getUsername() + " has terminated Order " + order.getOrderId();
		logService.logActivity(logMessage, user.getUsername());
	
		return terminatedOrder;
	}	
	
}