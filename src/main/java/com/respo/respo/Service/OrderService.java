package com.respo.respo.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Autowired
	CarRepository crepo;

	@Autowired
	PaymentRepository paymentRepo; // Add @Autowired annotation here

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

		// Manually update active status if start date matches today's date
		if (savedOrder.getStartDate().equals(LocalDate.now())) {
			savedOrder.setActive(true);
			orepo.save(savedOrder); // Save the updated order immediately
		}

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
		return orepo.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order " + orderId + " does not exist"));
	}

	public List<OrderEntity> getOrdersByCar(CarEntity car) {
		return orepo.findByCar(car);
	}

	public Map<String, Object> extendOrder(int orderId, LocalDate newEndDate) {
		// Find the order by its ID
		OrderEntity order = orepo.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order " + orderId + " does not exist"));

		// Get the current end date of the order
		LocalDate currentEndDate = order.getEndDate();

		// Check if the new end date is after the current end date
		if (newEndDate.isBefore(currentEndDate)) {
			throw new IllegalArgumentException("New end date must be after the current end date");
		}

		// Calculate the additional days for the extension
		long additionalDays = currentEndDate.until(newEndDate).getDays();

		// Get the car's daily rate
		CarEntity car = order.getCar();
		float dailyRate = car.getRentPrice();

		// Calculate the cost of the extension (only for the additional days)
		float extensionCost = dailyRate * additionalDays;

		// Update the total price in the order
		float newTotalPrice = order.getTotalPrice() + extensionCost;
		order.setTotalPrice(newTotalPrice); // Update the total price
		order.setEndDate(newEndDate); // Update the order's end date

		// Save the updated order
		orepo.save(order);

		// Create a payment record **only for the extension cost**
		PaymentEntity payment = new PaymentEntity();
		payment.setOrder(order);
		payment.setAmount(extensionCost); // Only the cost of the extension
		payment.setPaymentDate(LocalDateTime.now());
		payment.setPaymentMethod("PayPal"); // Assume PayPal for simplicity
		payment.setStatus(1); // Mark payment as completed

		// Save the payment record
		paymentRepo.save(payment);

		// Log the order extension
		String logMessage = "Order " + order.getOrderId() + " has been extended from " +
				currentEndDate + " to " + newEndDate + ". Additional Days: " + additionalDays;
		logService.logActivity(logMessage, order.getUser().getUsername());

		// Prepare the response to return both the updated order and the extension cost
		Map<String, Object> response = new HashMap<>();
		response.put("updatedOrder", order); // Return the updated order
		response.put("extensionCost", extensionCost); // Include the extension cost in the response

		return response;
	}

	public OrderEntity terminateOrder(int orderId) {
		OrderEntity order = orepo.findById(orderId)
				.orElseThrow(() -> new NoSuchElementException("Order " + orderId + " does not exist"));
	
		// Set order as terminated and capture the current date in the Philippines timezone
		order.setTerminated(true);
		order.setActive(false);  // Set the order as inactive
		ZonedDateTime philippinesTime = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
		order.setTerminationDate(philippinesTime.toLocalDate());  // Store only the date part
	
		// Optionally, set car and user status to non-active
		CarEntity car = order.getCar();
		if (car != null) {
			car.setRented(false);  // Set car as not rented
		}
	
		UserEntity user = order.getUser();
		if (user != null) {
			user.setRenting(false);  // Set user as not renting
		}
	
		// Get the most recent payment
		PaymentEntity latestPayment = order.getPayments().stream()
				.filter(payment -> !payment.isRefunded())  // Only consider non-refunded payments
				.max(Comparator.comparingInt(PaymentEntity::getPaymentId))
				.orElseThrow(() -> new NoSuchElementException("No valid payment found for order " + orderId));
	
		// Calculate the refund amount based on the days between start and termination date
		long daysDifference = ChronoUnit.DAYS.between(order.getStartDate(), order.getTerminationDate());
		float refundPercentage = 0.0f;
	
		if (daysDifference >= 3) {
			refundPercentage = 0.85f; // 85% refund
		} else if (daysDifference >= 1 && daysDifference <= 2) {
			refundPercentage = 0.50f; // 50% refund
		} else {
			refundPercentage = 0.0f; // No refund if termination is on the start date
		}
	
		// Calculate the total refund amount
		float totalPaidAmount = order.getPayments().stream()
				.filter(payment -> !payment.isRefunded())
				.map(PaymentEntity::getAmount)
				.reduce(0.0f, Float::sum);
	
		float refundAmount = totalPaidAmount * refundPercentage;
	
		// Update the refundable field for the latest payment and mark it as refunded
		latestPayment.setRefunded(true);
		latestPayment.setRefundDate(LocalDateTime.now());
		latestPayment.setRefundable(latestPayment.getAmount() * refundPercentage); // Set refundable amount
		paymentRepo.save(latestPayment);  // Save the updated payment entity
	
		// Save the order with updated status and refund amount
		order.setTotalPrice(order.getTotalPrice() - refundAmount); // Update the total price
		return orepo.save(order);  // Return the updated order
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

		// Check payment method and set active and status accordingly
		if ("PayPal".equalsIgnoreCase(paymentOption) || "PayMongo".equalsIgnoreCase(paymentOption)) {
			order.setStatus(1); // Assuming '1' indicates a successful payment
			order.setActive(true);
		}

		// Create a payment record associated with the correct payment method
		PaymentEntity payment = new PaymentEntity();
		payment.setOrder(order);
		payment.setAmount(order.getTotalPrice());
		payment.setPaymentMethod(paymentOption);
		payment.setStatus(status);

		// Set payment status to '1' if PayPal or PayMongo
		if ("PayPal".equalsIgnoreCase(paymentOption) || "PayMongo".equalsIgnoreCase(paymentOption)) {
			payment.setStatus(1);
		}

		paymentRepo.save(payment);

		orepo.save(order); // Save updated order
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

	public OrderEntity getOrderByReferenceNumber(String referenceNumber) {
		return orepo.findByReferenceNumber(referenceNumber)
				.orElseThrow(() -> new NoSuchElementException("Order not found with reference: " + referenceNumber));
	}

	public void updateActiveStatusIfStartDateMatches() {
		LocalDate currentDate = LocalDate.now(); // Get today's date (only date, no time)

		List<OrderEntity> allOrders = orepo.findAll(); // Get all orders

		for (OrderEntity order : allOrders) {
			// Ensure we are only comparing the date part (no time)
			if (order.getStartDate().equals(currentDate)) {
				order.setActive(true); // Set active status to true if dates match
				orepo.save(order); // Save the updated order
			}
		}
	}
}
