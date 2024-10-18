package com.respo.respo.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.OrderEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Repository.CarRepository;
import com.respo.respo.Repository.OrderRepository;
import com.respo.respo.Repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarService {

	@Autowired
	CarRepository crepo;

	@Autowired
	UserRepository userRepository;

	@Autowired
	OrderRepository orderRepository;

	public List<CarEntity> getAllCarsWithOwner() {
		return crepo.findAll(); // Implement this method in your repository
	}

	// Create a car and assign an owner
	public CarEntity insertCar(CarEntity car, UserEntity owner) {
		car.setOwner(owner); // Set the owner of the car
		userRepository.save(owner); // Save the updated owner
		return crepo.save(car); // Save the car
	}

	// Read
	public List<CarEntity> getAllCars() {
		return crepo.findAll();
	}

	// Update
	public CarEntity updateCar(int carId, CarEntity newCarDetails) {
		CarEntity car = crepo.findById(carId)
				.orElseThrow(() -> new NoSuchElementException("Car " + carId + " does not exist!"));

		if (newCarDetails.getCarDescription() != null) {
			car.setCarDescription(newCarDetails.getCarDescription());
		}

		if (newCarDetails.getRentPrice() != 0) {
			car.setRentPrice(newCarDetails.getRentPrice());
		}

		if (newCarDetails.getAddress() != null) {
			car.setAddress(newCarDetails.getAddress());
		}

		if (newCarDetails.getColor() != null) {
			car.setColor(newCarDetails.getColor());
		}
	
		if (newCarDetails.getPlateNumber() != null) {
			car.setPlateNumber(newCarDetails.getPlateNumber());
		}
	
		if (newCarDetails.getMaxSeatingCapacity() != 0) {
			car.setMaxSeatingCapacity(newCarDetails.getMaxSeatingCapacity());
		}

		if (newCarDetails.getCarImage() != null && newCarDetails.getCarImage().length > 0) {
			car.setCarImage(newCarDetails.getCarImage());
		}

		return crepo.save(car);
	}

	// Delete
	public String deleteCar(int carId) {
		CarEntity car = crepo.findById(carId)
				.orElseThrow(() -> new NoSuchElementException("Car " + carId + " does not exist"));

		if (car.isDeleted()) {
			return "Car #" + carId + " is already deleted!";
		} else {
			car.setDeleted(true);
			crepo.save(car);
			return "Car #" + carId + " has been deleted";
		}
	}

	public CarEntity approveCar(int carId) {
        CarEntity car = crepo.findById(carId)
                .orElseThrow(() -> new NoSuchElementException("Car " + carId + " does not exist!"));
        car.setApproved(true);
        return crepo.save(car);
    }

	public List<CarEntity> findCarsByUserId(int userId) {
		return crepo.findByOwnerId(userId);
	}

	public CarEntity getCarById(int carId) {
		return crepo.findById(carId).orElseThrow(() -> new NoSuchElementException("Car not found with id: " + carId));
	}

	@Scheduled(cron = "0 * * * * *") // Runs at the start of every minute
	@Transactional // Ensure changes are persisted
	public void updateCarRentalStatus() {
		LocalDate currentDate = LocalDate.now();
		List<CarEntity> rentedCars = new ArrayList<>(crepo.findAllByIsRented(true)); // Make a copy of the list
		System.out.println("Current Date: " + currentDate);

		for (int i = 0; i < rentedCars.size(); i++) {
			CarEntity car = rentedCars.get(i);
			List<OrderEntity> orders = car.getOrders();

			if (!orders.isEmpty()) {
				// Assuming Order IDs are generated sequentially, the last inserted order will
				// have the highest ID
				OrderEntity latestOrder = orders.stream()
						.max(Comparator.comparingInt(OrderEntity::getOrderId))
						.orElse(null);
				if (latestOrder != null) {
					LocalDate endDate = latestOrder.getEndDate();
					System.out.println("Latest OrderId: " + latestOrder.getOrderId());
					System.out.println("Car ID: " + car.getCarId() + " Order End Date: " + endDate);

					if (endDate == null) {
						System.out.println("End Date is null for order: " + latestOrder.getOrderId());
					}

					if (endDate != null && endDate.isBefore(currentDate)) {
						if (latestOrder.isActive()) { // Only update if the order is currently active
							latestOrder.setActive(false); // Set isActive to false
							latestOrder.setStatus(3); // Set status to 2 indicating end of rental
							orderRepository.save(latestOrder);
						}
						if (car.isRented()) { // Check if the car is still rented
							car.setRented(false); // Set isRented to false
							UserEntity user = latestOrder.getUser();
							System.out.println("User from Order: " + (user != null ? user.getUserId() : "null"));
							if (user != null && user.isRenting()) {
								user.setRenting(false); // Set user's isRenting to false
								System.out.println("User is renting: " + user.isRenting());
								userRepository.save(user); // Save the user state
							}
							crepo.save(car); // Save the car state
						}
					}
				}
			}
		}
	}

	public List<CarEntity> getAllCarsWithOrders() {
		List<CarEntity> cars = crepo.findAll();
		return cars.stream()
				.map(car -> {
					// Assuming you have methods to fetch the latest order or format the car data.
					car.setOrders(car.getOrders()); // This line assumes orders are eagerly fetched.
					return car;
				})
				.collect(Collectors.toList());
	}

	public List<DateRange> getActiveOrderDateRangesForCar(int carId) {
        // Fetch the car by ID
        CarEntity car = crepo.findById(carId)
                .orElseThrow(() -> new NoSuchElementException("Car not found with id: " + carId));

        // Filter out the active orders and map them to DateRange objects
        return car.getOrders().stream()
                .filter(OrderEntity::isActive)
                .map(order -> new DateRange(order.getStartDate(), order.getEndDate()))
                .collect(Collectors.toList());
    }

	public static class DateRange {
        private LocalDate startDate;
        private LocalDate endDate;

        public DateRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        @Override
        public String toString() {
            return "DateRange{" +
                    "startDate=" + startDate +
                    ", endDate=" + endDate +
                    '}';
        }
    }
	
}
