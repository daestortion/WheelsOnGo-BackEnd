package com.respo.respo.Controller;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = ("http://main--wheelsongo.netlify.app"))
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
		@RequestParam("carDescription") String carDescription,
		@RequestParam("color") String color,
		@RequestParam("plateNumber") String plateNumber,
		@RequestParam("maxSeatingCapacity") int maxSeatingCapacity,
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
		car.setCarDescription(carDescription);
		car.setColor(color);
		car.setPlateNumber(plateNumber);
		car.setMaxSeatingCapacity(maxSeatingCapacity);

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
	
	// U - Update
	@PutMapping("/updateCar")
	public ResponseEntity<CarEntity> updateCar(@RequestBody CarEntity newCarDetails) {
		try {
			CarEntity updatedCar = cserv.updateCar(newCarDetails.getCarId(), newCarDetails);
			return new ResponseEntity<>(updatedCar, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
	

	// D - Delete 
	@PutMapping("/deleteCar/{carId}")
	public ResponseEntity<String> deleteCar(@PathVariable int carId) {
        try {
            String result = cserv.deleteCar(carId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	@PutMapping("/approveCar/{carId}")
    public ResponseEntity<String> approveCar(@PathVariable int carId) {
        try {
            cserv.approveCar(carId);
            return new ResponseEntity<>("Car approved successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
	
    @GetMapping("/allCarsWithOrders")
    public ResponseEntity<List<CarEntity>> getAllCarsWithOrders() {
        try {
            List<CarEntity> cars = cserv.getAllCarsWithOrders();
            return ResponseEntity.ok(cars);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

	// Add this method in CarController
	@GetMapping("/getCarById/{carId}")
	public ResponseEntity<CarEntity> getCarById(@PathVariable int carId) {
		try {
			CarEntity car = cserv.getCarById(carId);
			return new ResponseEntity<>(car, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

}
