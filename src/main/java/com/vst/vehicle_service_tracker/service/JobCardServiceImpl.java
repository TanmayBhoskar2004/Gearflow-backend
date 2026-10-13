package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.*;
import com.vst.vehicle_service_tracker.entity.*;
import com.vst.vehicle_service_tracker.exception.ResourceNotFoundException;
import com.vst.vehicle_service_tracker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JobCardServiceImpl implements JobCardService {

    private final JobCardRepository jobCardRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final ServiceStatusHistoryRepository statusHistoryRepository;
    private final JobTechnicianRepository jobTechnicianRepository;
    private final TechnicianRepository technicianRepository;
    private final NotificationService notificationService;   // ✅ NEW (Day 8)

    // Valid transitions: current status -> allowed next statuses
    private static final Map<JobStatus, List<JobStatus>> VALID_TRANSITIONS = Map.of(
            JobStatus.RECEIVED, List.of(JobStatus.INSPECTION),
            JobStatus.INSPECTION, List.of(JobStatus.REPAIRING),
            JobStatus.REPAIRING, List.of(JobStatus.PARTS_REPLACEMENT, JobStatus.TESTING),
            JobStatus.PARTS_REPLACEMENT, List.of(JobStatus.REPAIRING, JobStatus.TESTING),
            JobStatus.TESTING, List.of(JobStatus.READY_FOR_DELIVERY, JobStatus.REPAIRING),
            JobStatus.READY_FOR_DELIVERY, List.of(JobStatus.DELIVERED),
            JobStatus.DELIVERED, List.of()
    );

    public JobCardServiceImpl(JobCardRepository jobCardRepository,
                              ServiceRequestRepository serviceRequestRepository,
                              UserRepository userRepository,
                              ServiceStatusHistoryRepository statusHistoryRepository,
                              JobTechnicianRepository jobTechnicianRepository,
                              TechnicianRepository technicianRepository,
                              NotificationService notificationService) {
        this.jobCardRepository = jobCardRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.jobTechnicianRepository = jobTechnicianRepository;
        this.technicianRepository = technicianRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public JobCardResponse createJobCard(String advisorUsername, JobCardRequest request) {
        ServiceRequest sr = serviceRequestRepository.findById(request.getServiceRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));

        User advisor = userRepository.findByUsername(advisorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Advisor not found"));

        if ("APPROVED".equalsIgnoreCase(sr.getStatus())) {
            throw new RuntimeException("Job card already exists for this service request");
        }

        JobCard jobCard = new JobCard();
        jobCard.setServiceRequest(sr);
        jobCard.setAdvisor(advisor);
        jobCard.setReceivedDate(LocalDateTime.now());
        jobCard.setEstimatedCompletion(request.getEstimatedCompletion());
        jobCard.setStatus(JobStatus.RECEIVED);
        jobCard.setLaborCharge(request.getLaborCharge() != null ? request.getLaborCharge() : BigDecimal.ZERO);
        jobCard.setOtherCharges(request.getOtherCharges() != null ? request.getOtherCharges() : BigDecimal.ZERO);
        jobCard.setTax(request.getTax() != null ? request.getTax() : BigDecimal.ZERO);
        jobCard.setDiscount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO);

        JobCard saved = jobCardRepository.save(jobCard);

        sr.setStatus("APPROVED");
        serviceRequestRepository.save(sr);

        recordHistory(saved, JobStatus.RECEIVED, advisor, "Job card created");

        // ✅ NEW (Day 8): notify the customer that the job card was created
        User customerUser = sr.getVehicle().getCustomer().getUser();
        notificationService.sendNotification(
                customerUser,
                "Your service request has been approved. Job Card #" + saved.getId() + " has been created."
        );

        return mapToResponse(saved);
    }

    @Override
    public List<JobCardResponse> getAllJobCards() {
        return jobCardRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public JobCardResponse getJobCardById(Long id) {
        JobCard jc = jobCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job card not found: " + id));
        return mapToResponse(jc);
    }

    @Override
    public List<JobCardResponse> getJobCardsByStatus(String status) {
        JobStatus jobStatus = JobStatus.valueOf(status.toUpperCase());
        return jobCardRepository.findByStatus(jobStatus).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    // ===================== DAY 5 =====================

    @Override
    @Transactional
    public JobCardResponse assignTechnician(Long jobCardId, Long technicianId, String advisorUsername) {
        JobCard jc = jobCardRepository.findById(jobCardId)
                .orElseThrow(() -> new ResourceNotFoundException("Job card not found"));

        Technician tech = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found"));

        // Check if already assigned
        boolean alreadyAssigned = jobTechnicianRepository.findByJobCardId(jobCardId)
                .stream()
                .anyMatch(jt -> jt.getTechnician().getId().equals(technicianId));

        if (alreadyAssigned) {
            throw new RuntimeException("Technician already assigned to this job card");
        }

        JobTechnician jt = new JobTechnician();
        jt.setJobCard(jc);
        jt.setTechnician(tech);
        jt.setAssignmentDate(LocalDateTime.now());
        jobTechnicianRepository.save(jt);

        return mapToResponse(jc);
    }

    @Override
    @Transactional
    public JobCardResponse updateStatus(Long jobCardId, String newStatus, String username, String note) {
        JobCard jc = jobCardRepository.findById(jobCardId)
                .orElseThrow(() -> new ResourceNotFoundException("Job card not found"));

        JobStatus newJobStatus;
        try {
            newJobStatus = JobStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + newStatus);
        }

        JobStatus currentStatus = jc.getStatus();

        if (!isValidTransition(currentStatus, newJobStatus)) {
            throw new RuntimeException("Invalid status transition: " + currentStatus + " → " + newJobStatus);
        }

        jc.setStatus(newJobStatus);
        jobCardRepository.save(jc);

        User changer = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        recordHistory(jc, newJobStatus, changer, note);

        // ✅ NEW (Day 8): notify the customer about the status change
        User customerUser = jc.getServiceRequest().getVehicle().getCustomer().getUser();
        String message = String.format(
                "Your vehicle %s status has been updated to: %s",
                jc.getServiceRequest().getVehicle().getRegistrationNumber(),
                newJobStatus.name().replace("_", " ")
        );
        notificationService.sendNotification(customerUser, message);

        return mapToResponse(jc);
    }

    @Override
    public List<ServiceStatusHistoryResponse> getStatusHistory(Long jobCardId) {
        return statusHistoryRepository.findByJobCardIdOrderByChangedAtDesc(jobCardId)
                .stream().map(h -> {
                    ServiceStatusHistoryResponse r = new ServiceStatusHistoryResponse();
                    r.setId(h.getId());
                    r.setStatus(h.getStatus().name());
                    r.setChangedByUsername(h.getChangedBy().getUsername());
                    r.setChangedAt(h.getChangedAt());
                    return r;
                }).collect(Collectors.toList());
    }

    @Override
    public List<JobCardResponse> getMyAssignedJobs(String technicianUsername) {
        var user = userRepository.findByUsername(technicianUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Technician tech = technicianRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Technician profile not found"));

        return jobTechnicianRepository.findByTechnicianId(tech.getId())
                .stream()
                .map(JobTechnician::getJobCard)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===================== HELPERS =====================

    private boolean isValidTransition(JobStatus from, JobStatus to) {
        List<JobStatus> allowed = VALID_TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    private void recordHistory(JobCard jc, JobStatus status, User user, String note) {
        ServiceStatusHistory history = new ServiceStatusHistory();
        history.setJobCard(jc);
        history.setStatus(status);
        history.setChangedBy(user);
        history.setChangedAt(LocalDateTime.now());
        statusHistoryRepository.save(history);
    }

    private JobCardResponse mapToResponse(JobCard jc) {
        JobCardResponse r = new JobCardResponse();
        r.setId(jc.getId());
        r.setServiceRequestId(jc.getServiceRequest().getId());
        r.setRegistrationNumber(jc.getServiceRequest().getVehicle().getRegistrationNumber());
        Customer c = jc.getServiceRequest().getVehicle().getCustomer();
        r.setCustomerName(c.getFirstName() + " " + c.getLastName());
        r.setAdvisorUsername(jc.getAdvisor().getUsername());
        r.setReceivedDate(jc.getReceivedDate());
        r.setEstimatedCompletion(jc.getEstimatedCompletion());
        r.setStatus(jc.getStatus().name());
        r.setLaborCharge(jc.getLaborCharge());
        r.setOtherCharges(jc.getOtherCharges());
        r.setTax(jc.getTax());
        r.setDiscount(jc.getDiscount());
        r.setDescription(jc.getServiceRequest().getDescription());
        return r;
    }
}