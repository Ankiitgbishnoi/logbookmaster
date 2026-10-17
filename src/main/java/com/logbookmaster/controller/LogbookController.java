package com.logbookmaster.controller;

import com.logbookmaster.model.LogbookEntry;
import com.logbookmaster.model.User;
import com.logbookmaster.model.Vehicle;
import com.logbookmaster.repository.LogbookEntryRepository;
import com.logbookmaster.repository.UserRepository;
import com.logbookmaster.repository.VehicleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LogbookController {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final LogbookEntryRepository logbookEntryRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LogbookController(UserRepository userRepository,
                             VehicleRepository vehicleRepository,
                             LogbookEntryRepository logbookEntryRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.logbookEntryRepository = logbookEntryRepository;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "app", "Logbook Master",
            "type", "Java Spring Boot",
            "database", "H2"
        );
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> payload) {
        String name = payload.getOrDefault("name", "").trim();
        String email = payload.getOrDefault("email", "").trim().toLowerCase();
        String password = payload.getOrDefault("password", "");

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Name, email and password are required."));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "An account with that email already exists."));
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Registration successful.",
            "user", toPublicUser(user)
        ));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload) {
        String email = payload.getOrDefault("email", "").trim().toLowerCase();
        String password = payload.getOrDefault("password", "");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid credentials."));
        }

        User user = userOpt.get();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Login successful.",
            "user", toPublicUser(user)
        ));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(@RequestParam(required = false) String email) {
        String targetEmail = (email == null || email.isBlank()) ? "demo@logbookmaster.com" : email.trim().toLowerCase();
        User user = userRepository.findByEmail(targetEmail)
            .orElseGet(() -> userRepository.findAll().stream().findFirst().orElseThrow());

        List<Vehicle> vehicles = vehicleRepository.findByUserId(user.getId());
        List<LogbookEntry> entries = logbookEntryRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalVehicles", vehicles.size());
        summary.put("totalEntries", entries.size());
        summary.put("driverHours", 3.33);
        summary.put("restHours", 10.0);

        Map<String, Object> response = new HashMap<>();
        response.put("user", toPublicUser(user));
        response.put("vehicles", vehicles.stream().map(this::toVehicleMap).toList());
        response.put("entries", entries.stream().map(this::toEntryMap).toList());
        response.put("stats", summary);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<Map<String, Object>>> getVehicles(@RequestParam String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
            .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return ResponseEntity.ok(vehicleRepository.findByUserId(user.getId()).stream().map(this::toVehicleMap).toList());
    }

    @PostMapping("/vehicles")
    public ResponseEntity<Map<String, Object>> createVehicle(@RequestBody Map<String, String> payload) {
        String email = payload.getOrDefault("email", "").trim().toLowerCase();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Vehicle vehicle = new Vehicle();
        vehicle.setUser(user);
        vehicle.setRegistration(payload.getOrDefault("registration", ""));
        vehicle.setMake(payload.getOrDefault("make", ""));
        vehicle.setModel(payload.getOrDefault("model", ""));
        vehicle.setYear(payload.getOrDefault("year", ""));
        vehicle.setStatus(payload.getOrDefault("status", "Active"));
        vehicle = vehicleRepository.save(vehicle);

        return ResponseEntity.ok(Map.of("success", true, "vehicle", toVehicleMap(vehicle)));
    }

    @GetMapping("/logbooks")
    public ResponseEntity<List<Map<String, Object>>> getLogbooks(@RequestParam String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
            .orElseThrow(() -> new IllegalArgumentException("User not found."));

        return ResponseEntity.ok(logbookEntryRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
            .stream().map(this::toEntryMap).toList());
    }

    @PostMapping("/logbooks")
    public ResponseEntity<Map<String, Object>> createLogbook(@RequestBody Map<String, String> payload) {
        String email = payload.getOrDefault("email", "").trim().toLowerCase();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));

        LogbookEntry entry = new LogbookEntry();
        entry.setUser(user);
        entry.setDriverName(payload.getOrDefault("driverName", ""));
        entry.setDriverId(payload.getOrDefault("driverId", ""));
        entry.setVehicleRego(payload.getOrDefault("vehicleRego", ""));
        entry.setSecurityCode(payload.getOrDefault("securityCode", ""));
        entry.setStartTime(payload.getOrDefault("startTime", "06:30"));
        entry.setEndTime(payload.getOrDefault("endTime", "09:00"));
        entry.setNatureOfWork(payload.getOrDefault("natureOfWork", "Driving"));
        entry.setDescription(payload.getOrDefault("description", ""));
        entry.setSignatureData(payload.getOrDefault("signatureData", ""));
        entry.setCreatedAt(LocalDateTime.now());

        String vehicleRego = payload.getOrDefault("vehicleRego", "");
        if (!vehicleRego.isBlank()) {
            Vehicle vehicle = vehicleRepository.findByUserId(user.getId()).stream()
                .filter(v -> v.getRegistration().equalsIgnoreCase(vehicleRego))
                .findFirst()
                .orElse(null);
            entry.setVehicle(vehicle);
        }

        entry = logbookEntryRepository.save(entry);
        return ResponseEntity.ok(Map.of("success", true, "entry", toEntryMap(entry)));
    }

    private Map<String, Object> toPublicUser(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("createdAt", user.getCreatedAt());
        return map;
    }

    private Map<String, Object> toVehicleMap(Vehicle vehicle) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", vehicle.getId());
        map.put("registration", vehicle.getRegistration());
        map.put("make", vehicle.getMake());
        map.put("model", vehicle.getModel());
        map.put("year", vehicle.getYear());
        map.put("status", vehicle.getStatus());
        return map;
    }

    private Map<String, Object> toEntryMap(LogbookEntry entry) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", entry.getId());
        map.put("driverName", entry.getDriverName());
        map.put("driverId", entry.getDriverId());
        map.put("vehicleRego", entry.getVehicleRego());
        map.put("securityCode", entry.getSecurityCode());
        map.put("startTime", entry.getStartTime());
        map.put("endTime", entry.getEndTime());
        map.put("natureOfWork", entry.getNatureOfWork());
        map.put("description", entry.getDescription());
        map.put("signatureData", entry.getSignatureData());
        map.put("createdAt", entry.getCreatedAt());
        return map;
    }
}
