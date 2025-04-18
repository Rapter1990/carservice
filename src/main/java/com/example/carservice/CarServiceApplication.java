package com.example.carservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class named {@link CarServiceApplication} to start the Car Service application.
 */
@SpringBootApplication
public class CarServiceApplication {

	/**
	 * Main method to start the Spring Boot application.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(CarServiceApplication.class, args);
	}

}
