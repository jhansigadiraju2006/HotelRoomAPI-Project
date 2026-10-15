package com.example.demo;

import jakarta.validation.constraints.*;

public class Room {
    private Long id;

    @NotBlank(message = "name is required")
    @Size(min = 3, max = 60, message = "name must be between 3 and 60 characters")
    private String name;

    @NotBlank(message = "location is required")
    @Size(min = 2, max = 40, message = "location must be between 2 and 40 characters")
    private String location;

    @NotBlank(message = "type is required")
    private String type;

    @DecimalMin(value = "0.01", message = "pricePerNight must be greater than 0")
    @DecimalMax(value = "100000.00", message = "pricePerNight must not exceed 100000")
    private double pricePerNight;

    @Min(value = 1, message = "capacity must be at least 1")
    @Max(value = 10, message = "capacity must not exceed 10")
    private int capacity;

    private boolean available;

    @NotBlank(message = "roomNumber is required")
    @Size(min = 4, max = 20, message = "roomNumber must be between 4 and 20 characters")
    private String roomNumber;

    public Room() {}

    public Room(Long id, String name, String location, String type,
                double pricePerNight, int capacity, boolean available, String roomNumber) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.available = available;
        this.roomNumber = roomNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
}
