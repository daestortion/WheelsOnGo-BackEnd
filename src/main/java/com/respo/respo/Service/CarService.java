package com.respo.respo.Service;

import java.time.LocalDate;
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
import com.respo.respo.Repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarService {

	@Autowired
	CarRepository crepo;
	
	@Autowired
    UserRepository userRepository;
    public List<CarEntity> getAllCarsWithOwner() {
        return crepo.findAll(); // Implement this method in your repository
    }
    // Create a car and assign an owner
    public CarEntity insertCar(CarEntity car, UserEntity owner) {
        car.setOwner(owner);  // Set the owner of the car
        userRepository.save(owner);  // Save the updated owner
        return crepo.save(car);  // Save the car
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


	public List<CarEntity> findCarsByUserId(int userId) {
		return crepo.findByOwnerId(userId);
	}
	
	public CarEntity getCarById(int carId) {
	    return crepo.findById(carId).orElseThrow(() -> new NoSuchElementException("Car not found with id: " + carId));
	}
	
    public void updateCarRentalStatus() {
        LocalDate currentDate = LocalDate.now();
        List<CarEntity> rentedCars = crepo.findAllByIsRented(true);
        System.out.println("Current Date: " + currentDate);

        rentedCars.forEach(car -> {
            car.getOrders().forEach(order -> {
                LocalDate endDate = order.getEndDate(); // Assuming endDate is a LocalDate
                System.out.println("Car ID: " + car.getCarId() + " Order End Date: " + endDate);
                
                if (endDate != null && endDate.isBefore(currentDate)) {
                    if (order.isActive()) { // Only update if the order is currently active
                        order.setActive(false); // Set isActive to false
                    }
                    if (car.isRented()) { // Check if the car is still rented
                        car.setRented(false); // Set isRented to false
                        crepo.save(car); // Save the car state
                    }
                }
            });
        });
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
}
