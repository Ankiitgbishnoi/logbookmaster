package com.logbookmaster;

import com.logbookmaster.model.LogbookEntry;
import com.logbookmaster.model.User;
import com.logbookmaster.model.Vehicle;
import com.logbookmaster.repository.LogbookEntryRepository;
import com.logbookmaster.repository.UserRepository;
import com.logbookmaster.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(UserRepository userRepository,
                           VehicleRepository vehicleRepository,
                           LogbookEntryRepository logbookEntryRepository,
                           PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User demo = new User();
                demo.setName("Demo Driver");
                demo.setEmail("demo@logbookmaster.com");
                demo.setPassword(passwordEncoder.encode("password123"));
                demo.setCreatedAt(LocalDateTime.now());
                demo = userRepository.save(demo);

                Vehicle vehicle = new Vehicle();
                vehicle.setUser(demo);
                vehicle.setRegistration("ABC-123");
                vehicle.setMake("Volvo");
                vehicle.setModel("FH16");
                vehicle.setYear("2024");
                vehicle.setStatus("Active");
                vehicleRepository.save(vehicle);

                LogbookEntry entry = new LogbookEntry();
                entry.setUser(demo);
                entry.setVehicle(vehicle);
                entry.setDriverName("Demo Driver");
                entry.setDriverId("12345678");
                entry.setVehicleRego("ABC-123");
                entry.setSecurityCode("SEC-2026-001");
                entry.setStartTime("06:30");
                entry.setEndTime("09:00");
                entry.setNatureOfWork("Driving — delivery run");
                entry.setDescription("Regional freight movement and daily delivery run.");
                entry.setSignatureData("");
                entry.setCreatedAt(LocalDateTime.now());
                logbookEntryRepository.save(entry);
            }
        };
    }
}
