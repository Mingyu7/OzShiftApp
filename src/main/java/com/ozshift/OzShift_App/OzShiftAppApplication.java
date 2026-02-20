package com.ozshift.OzShift_App;

import com.ozshift.OzShift_App.entity.User;
import com.ozshift.OzShift_App.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class OzShiftAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(OzShiftAppApplication.class, args);
        System.out.println("--run--");
	}

    @Bean
    public CommandLineRunner resetPasswords(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            List<User> users = userRepository.findAll();
            String encodedPassword = passwordEncoder.encode("1234");
            for (User user : users) {
                user.setPassword(encodedPassword);
                userRepository.save(user);
            }
            System.out.println("DEBUG: All user passwords have been reset to 1234");
        };
    }

}
