package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.ServiceRequestRequest;
import com.vst.vehicle_service_tracker.dto.ServiceRequestResponse;
import com.vst.vehicle_service_tracker.entity.Customer;
import com.vst.vehicle_service_tracker.entity.ServiceRequest;
import com.vst.vehicle_service_tracker.entity.Vehicle;
import com.vst.vehicle_service_tracker.exception.ResourceNotFoundException;
import com.vst.vehicle_service_tracker.repository.ServiceRequestRepository;
import com.vst.vehicle_service_tracker.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServiceRequestRepository requestRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerService customerService;

    public ServiceRequestServiceImpl(ServiceRequestRepository requestRepository,
                                     VehicleRepository vehicleRepository,
                                     CustomerService customerService) {
        this.requestRepository = requestRepository;
        this.vehicleRepository = vehicleRepository;
        this.customerService = customerService;
    }

    @Override
    public ServiceRequestResponse createRequest(String username, ServiceRequestRequest request) {
        Customer customer = customerService.getCustomerByUsername(username);

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!vehicle.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("You can only request service for your own vehicle");
        }

        ServiceRequest sr = new ServiceRequest();
        sr.setVehicle(vehicle);
        sr.setRequestDate(LocalDateTime.now());
        sr.setDescription(request.getDescription());
        sr.setStatus("PENDING");

        return mapToResponse(requestRepository.save(sr));
    }

    @Override
    public List<ServiceRequestResponse> getMyRequests(String username) {
        Customer customer = customerService.getCustomerByUsername(username);
        return requestRepository.findAll().stream()
                .filter(r -> r.getVehicle().getCustomer().getId().equals(customer.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceRequestResponse> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ServiceRequestResponse getRequestById(Long id) {
        ServiceRequest sr = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found: " + id));
        return mapToResponse(sr);
    }

    @Override
    public List<ServiceRequestResponse> getPendingRequests() {
        return requestRepository.findAll().stream()
                .filter(r -> "PENDING".equalsIgnoreCase(r.getStatus()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ServiceRequestResponse mapToResponse(ServiceRequest sr) {
        ServiceRequestResponse response = new ServiceRequestResponse();
        response.setId(sr.getId());
        response.setVehicleId(sr.getVehicle().getId());
        response.setRegistrationNumber(sr.getVehicle().getRegistrationNumber());
        response.setMake(sr.getVehicle().getMake());
        response.setModel(sr.getVehicle().getModel());
        response.setRequestDate(sr.getRequestDate());
        response.setDescription(sr.getDescription());
        response.setStatus(sr.getStatus());
        Customer c = sr.getVehicle().getCustomer();
        response.setCustomerName(c.getFirstName() + " " + c.getLastName());
        return response;
    }
}