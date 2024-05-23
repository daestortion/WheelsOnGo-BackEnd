package com.respo.respo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Repository.OrderRepository;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {

	@Autowired
	OrderRepository orepo;

	// Create
	// Create
	public OrderEntity insertOrder(OrderEntity order) {
		// Calculate the number of days between startDate and endDate
		long daysBetween = ChronoUnit.DAYS.between(order.getStartDate(), order.getEndDate());
		// Calculate the total price based on the number of days and car price per day
		float totalPrice = daysBetween * order.getCar().getRentPrice();
		order.setTotalPrice(totalPrice);

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
}
