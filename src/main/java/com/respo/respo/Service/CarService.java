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
}
