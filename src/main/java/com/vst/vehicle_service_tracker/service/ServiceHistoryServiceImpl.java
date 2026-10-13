package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.PartUsedResponse;
import com.vst.vehicle_service_tracker.dto.ServiceHistoryResponse;
import com.vst.vehicle_service_tracker.entity.*;
import com.vst.vehicle_service_tracker.exception.ResourceNotFoundException;
import com.vst.vehicle_service_tracker.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceHistoryServiceImpl implements ServiceHistoryService {

    private final JobCardRepository jobCardRepository;
    private final VehicleRepository vehicleRepository;
    private final JobPartRepository jobPartRepository;
    private final InvoiceRepository invoiceRepository;
    private final ServiceStatusHistoryRepository statusHistoryRepository;
    private final CustomerService customerService;

    public ServiceHistoryServiceImpl(JobCardRepository jobCardRepository,
                                     VehicleRepository vehicleRepository,
                                     JobPartRepository jobPartRepository,
                                     InvoiceRepository invoiceRepository,
                                     ServiceStatusHistoryRepository statusHistoryRepository,
                                     CustomerService customerService) {
        this.jobCardRepository = jobCardRepository;
        this.vehicleRepository = vehicleRepository;
        this.jobPartRepository = jobPartRepository;
        this.invoiceRepository = invoiceRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.customerService = customerService;
    }

    @Override
    public List<ServiceHistoryResponse> getMyVehicleHistory(String username, Long vehicleId) {
        Customer customer = customerService.getCustomerByUsername(username);
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!vehicle.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("You can only view history for your own vehicles");
        }

        return buildHistory(vehicle);
    }

    @Override
    public List<ServiceHistoryResponse> getVehicleHistory(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        return buildHistory(vehicle);
    }

    private List<ServiceHistoryResponse> buildHistory(Vehicle vehicle) {
        // Only consider DELIVERED jobs as completed history
        List<JobCard> jobs = jobCardRepository.findAll().stream()
                .filter(jc -> jc.getServiceRequest().getVehicle().getId().equals(vehicle.getId()))
                .filter(jc -> jc.getStatus() == JobStatus.DELIVERED)
                .collect(Collectors.toList());

        return jobs.stream().map(jc -> {
            ServiceHistoryResponse r = new ServiceHistoryResponse();
            r.setJobCardId(jc.getId());
            r.setRegistrationNumber(vehicle.getRegistrationNumber());
            r.setVehicleDescription(vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getYear() + ")");
            r.setServiceDescription(jc.getServiceRequest().getDescription());
            r.setReceivedDate(jc.getReceivedDate());
            r.setFinalStatus(jc.getStatus().name());

            // Get the delivered timestamp from status history
            List<ServiceStatusHistory> history = statusHistoryRepository
                    .findByJobCardIdOrderByChangedAtDesc(jc.getId());
            Optional<ServiceStatusHistory> deliveredEntry = history.stream()
                    .filter(h -> h.getStatus() == JobStatus.DELIVERED)
                    .findFirst();
            deliveredEntry.ifPresent(h -> r.setDeliveredDate(h.getChangedAt()));

            // Get invoice details
            invoiceRepository.findByJobCardId(jc.getId()).ifPresent(inv -> {
                r.setInvoiceTotal(inv.getTotal());
                r.setPaymentStatus(inv.getPaymentStatus());
            });

            // Get parts used
            List<JobPart> jobParts = jobPartRepository.findByJobCardId(jc.getId());
            List<PartUsedResponse> parts = jobParts.stream().map(jp -> {
                PartUsedResponse p = new PartUsedResponse();
                p.setPartName(jp.getPart().getName());
                p.setBrand(jp.getPart().getBrand());
                p.setQuantityUsed(jp.getQuantityUsed());
                p.setUnitPrice(jp.getUnitPriceAtTime());
                p.setWarrantyNotes(jp.getWarrantyNotes());
                return p;
            }).collect(Collectors.toList());
            r.setPartsUsed(parts);

            return r;
        }).collect(Collectors.toList());
    }
}