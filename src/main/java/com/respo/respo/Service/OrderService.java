package com.respo.respo.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.respo.respo.Entity.ActivityLogEntity;
import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.PaymentEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Repository.CarRepository;
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Repository.PaymentRepository;

@Service
public class OrderService {

	@Autowired
    UserService userService;

    @Autowired
    CarService carService;

	@Autowired
	OrderRepository orepo;
	CarRepository crepo;
	PaymentRepository paymentRepo;

	@Autowired
    ActivityLogService logService;

	@Autowired
    WalletService walletService; // Inject walletService to update wallet balances

	@Autowired
    PaymentService paymentService;
	
	// Insert order in `OrderService.java`
	public OrderEntity insertOrder(int userId, int carId, OrderEntity order) {
		UserEntity user = userService.getUserById(userId);
		CarEntity car = carService.getCarById(carId);

		order.setUser(user);
		order.setCar(car);
		order.setReferenceNumber(order.generateReferenceNumber());
		user.setRenting(true);
		car.setRented(true);

		OrderEntity savedOrder = orepo.save(order);

		// Use dynamic payment option based on order data
		paymentService.createPayment(savedOrder, order.getTotalPrice(), order.getPaymentOption(), null, 0);

		return savedOrder;
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
	
		// Set order as terminated and capture the current date in the Philippines timezone
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

	public void logOrderActivity(OrderEntity order) {
		CarEntity car = order.getCar();
		UserEntity user = order.getUser();
	
		// Format the log message with car ID, brand, model, username, and rental dates
		String logMessage = "Car " + car.getCarId() + ": " + car.getCarBrand() + " " + car.getCarModel() +
							" has been rented by " + user.getUsername() + " from " + order.getStartDate() +
							" to " + order.getEndDate();
	
		// Avoid duplicate logs by checking if the log already exists
		List<ActivityLogEntity> existingLogs = logService.getLogsByAction("has been rented by " + user.getUsername());
	
		boolean logExists = existingLogs.stream().anyMatch(log -> log.getAction().contains(car.getCarModel()));
	
		// Log the activity if no duplicate exists
		if (!logExists) {
			logService.logActivity(logMessage, user.getUsername());
		}
	}
	
	public void updatePaymentStatus(Map<String, Object> paymentData) {
		Integer orderId = (Integer) paymentData.get("orderId");
		String transactionId = (String) paymentData.get("transactionId");
		String paymentOption = (String) paymentData.get("paymentOption");
		int status = (int) paymentData.get("status");
	
		OrderEntity order = orepo.findById(orderId)
			.orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));
	
		// Set the reference number based on the payment method
		if ("PayPal".equalsIgnoreCase(paymentOption) && transactionId != null) {
			order.setReferenceNumber(transactionId); // Store PayPal transaction ID as reference number
		} else if ("Cash".equalsIgnoreCase(paymentOption)) {
			if (order.getReferenceNumber() == null || order.getReferenceNumber().isEmpty()) {
				order.setReferenceNumber(order.generateReferenceNumber()); // Generate a new reference number if not set
			}
		}
	
		// Create a payment record associated with the correct payment method
		PaymentEntity payment = new PaymentEntity();
		payment.setOrder(order);
		payment.setAmount(order.getTotalPrice());
		payment.setPaymentMethod(paymentOption);
		payment.setStatus(status);
		paymentRepo.save(payment);
	
		// Update order status to 'paid'
		order.setStatus(1); // Assuming '1' is paid status
		orepo.save(order);
	}
	

	public OrderEntity markAsReturned(int orderId) {
        OrderEntity order = orepo.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        // Mark the order as returned
        order.setReturned(true);
        order.setReturnDate(LocalDate.now());

        // Save the order
        OrderEntity savedOrder = orepo.save(order);

        // Log the car return activity
        String logMessage = order.getUser().getUsername() + " has successfully returned Car " +
                            " " + order.getCar().getCarId() + ": " + order.getCar().getCarBrand() + " " + 
                            order.getCar().getCarModel() + " in Order " + order.getOrderId() + ".";
        
        logService.logActivity(logMessage, order.getUser().getUsername());

        return savedOrder;
    }
}