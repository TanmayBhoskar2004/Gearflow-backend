package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.VehicleRequest;
import com.vst.vehicle_service_tracker.dto.VehicleResponse;
import com.vst.vehicle_service_tracker.entity.Customer;
import com.vst.vehicle_service_tracker.entity.Vehicle;
import com.vst.vehicle_service_tracker.exception.ResourceNotFoundException;
import com.vst.vehicle_service_tracker.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerService customerService;

    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              CustomerService customerService) {
        this.vehicleRepository = vehicleRepository;
        this.customerService = customerService;
    }

    @Override
    public VehicleResponse addVehicle(String username, VehicleRequest request) {
        Customer customer = customerService.getCustomerByUsername(username);

        vehicleRepository.findAll().stream()
                .filter(v -> v.getRegistrationNumber().equalsIgnoreCase(request.getRegistrationNumber()))
                .findFirst()
                .ifPresent(v -> {
                    throw new RuntimeException("Vehicle with this registration number already exists");
                });

        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setVin(request.getVin());

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToResponse(saved);
    }

    @Override
    public List<VehicleResponse> getMyVehicles(String username) {
        Customer customer = customerService.getCustomerByUsername(username);
        return vehicleRepository.findByCustomerId(customer.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public VehicleResponse getVehicleById(Long id, String username) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        Customer customer = customerService.getCustomerByUsername(username);

        // Only owner or Admin/Advisor can access
        if (!vehicle.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("You are not authorized to view this vehicle");
        }

        return mapToResponse(vehicle);
    }

    @Override
    public VehicleResponse updateVehicle(Long id, String username, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        Customer customer = customerService.getCustomerByUsername(username);

        if (!vehicle.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("You are not authorized to update this vehicle");
        }

        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setVin(request.getVin());

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public void deleteVehicle(Long id, String username) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        Customer customer = customerService.getCustomerByUsername(username);

        if (!vehicle.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("You are not authorized to delete this vehicle");
        }

        vehicleRepository.delete(vehicle);
    }

    @Override
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setRegistrationNumber(vehicle.getRegistrationNumber());
        response.setMake(vehicle.getMake());
        response.setModel(vehicle.getModel());
        response.setYear(vehicle.getYear());
        response.setVin(vehicle.getVin());
        response.setCustomerId(vehicle.getCustomer().getId());
        response.setCustomerName(
                vehicle.getCustomer().getFirstName() + " " + vehicle.getCustomer().getLastName()
        );
        return response;
    }
}