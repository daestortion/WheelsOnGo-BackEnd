package com.respo.respo.Service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Repository.CarRepository;
import com.respo.respo.Repository.UserRepository;

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

	    // Check and update carImage if it's not null or empty
	    if (newCarDetails.getCarImage() != null && newCarDetails.getCarImage().length > 0) {
	        car.setCarImage(newCarDetails.getCarImage());
	    }

	    // Check and update carOR if it's not null or empty
	    if (newCarDetails.getCarOR() != null && newCarDetails.getCarOR().length > 0) {
	        car.setCarOR(newCarDetails.getCarOR());
	    }

		// Check and update Address if it's not null or empty
	    if (newCarDetails.getAddress() != null && newCarDetails.getAddress().isEmpty()) {
	        car.setAddress(newCarDetails.getAddress());
	    }

		// Check and update rentPrice if it's not null or empty
	    if (newCarDetails.getRentPrice() != 0) {
			car.setRentPrice(newCarDetails.getRentPrice());
		}
		
	    // There is no need for a try-catch block here as findById with orElseThrow already handles the exception
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
}
