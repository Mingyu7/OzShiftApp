package com.ozshift.OzShift_App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OzShiftAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(OzShiftAppApplication.class, args);
        System.out.println("--run--");
	}

}
