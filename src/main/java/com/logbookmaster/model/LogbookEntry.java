package com.logbookmaster.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "logbook_entries")
public class LogbookEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "driver_name", nullable = false)
    private String driverName;

    @Column(name = "driver_id")
    private String driverId;

    @Column(name = "vehicle_rego", nullable = false)
    private String vehicleRego;

    @Column(name = "security_code")
    private String securityCode;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "nature_of_work")
    private String natureOfWork;

    @Column(name = "description", length = 2048)
    private String description;

    @Column(name = "signature_data", columnDefinition = "TEXT")
    private String signatureData;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getVehicleRego() { return vehicleRego; }
    public void setVehicleRego(String vehicleRego) { this.vehicleRego = vehicleRego; }

    public String getSecurityCode() { return securityCode; }
    public void setSecurityCode(String securityCode) { this.securityCode = securityCode; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getNatureOfWork() { return natureOfWork; }
    public void setNatureOfWork(String natureOfWork) { this.natureOfWork = natureOfWork; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSignatureData() { return signatureData; }
    public void setSignatureData(String signatureData) { this.signatureData = signatureData; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
