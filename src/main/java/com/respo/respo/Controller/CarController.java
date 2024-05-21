package com.respo.respo.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import com.respo.respo.Entity.CarEntity;
import com.respo.respo.Entity.UserEntity;
import com.respo.respo.Service.CarService;
import com.respo.respo.Service.UserService;

@RestController
@RequestMapping("/car")
@CrossOrigin(origins = ("http://localhost:3000"))
public class CarController {

	@Autowired
	CarService cserv;
	@Autowired
    UserService userService;
	
	@GetMapping("/print")
	public String itWorks() {
		return "It works";
	}


	public byte[] convertToBlob(MultipartFile file) {
		try {
			return file.getBytes();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

    // Create car with a specific owner
	@PostMapping(value = "/insertCar/{userId}", consumes = {"multipart/form-data"})
	public ResponseEntity<?> insertCar(
	    @PathVariable int userId,
	    @RequestParam("carBrand") String carBrand,
	    @RequestParam("carModel") String carModel,
	    @RequestParam("carYear") String carYear,
	    @RequestParam("address") String address,
	    @RequestParam("rentPrice") float rentPrice,
	    @RequestParam(value = "carImage", required = false) MultipartFile carImage,
	    @RequestParam(value = "carOR", required = false) MultipartFile carOR,
	    @RequestParam(value = "carCR", required = false) MultipartFile carCR
	) {
	    CarEntity car = new CarEntity();
	    car.setCarBrand(carBrand);
	    car.setCarModel(carModel);
	    car.setCarYear(carYear);
	    car.setAddress(address);
	    car.setRentPrice(rentPrice);
	    
	    if (carImage != null) {
	        car.setCarImage(convertToBlob(carImage));
	    }
	    if (carOR != null) {
	        car.setCarOR(convertToBlob(carOR));
	    }
	    if (carCR != null) {
	        car.setCarCR(convertToBlob(carCR));
	    }
	    
	    UserEntity user = userService.getUserById(userId);
	    return ResponseEntity.ok(cserv.insertCar(car, user));
	}




	// Read
	@GetMapping("/getAllCars")
	public List<CarEntity> getAllCars() {
		return cserv.getAllCars();
	}
	
	// U - Update a user record
	@PutMapping("/updateCar")
	public CarEntity updateCar(@RequestParam int carId, @RequestBody CarEntity newCarDetails) {
		return cserv.updateCar(carId, newCarDetails);
	}

	// D - Delete a user record
	@DeleteMapping("/deleteCar/{carId}")
	public String deleteCar(@PathVariable int carId) {
		return cserv.deleteCar(carId);
	}


	@GetMapping("/getAllCarsForUser/{userId}")
	public ResponseEntity<List<CarEntity>> getAllCarsForUser(@PathVariable int userId) {
		try {
			List<CarEntity> cars = cserv.findCarsByUserId(userId);
			return ResponseEntity.ok(cars);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
