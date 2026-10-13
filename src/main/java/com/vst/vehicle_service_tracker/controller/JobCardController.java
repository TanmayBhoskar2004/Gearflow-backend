package com.vst.vehicle_service_tracker.controller;

import com.vst.vehicle_service_tracker.dto.*;
import com.vst.vehicle_service_tracker.service.JobCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-cards")
public class JobCardController {

    private final JobCardService jobCardService;

    public JobCardController(JobCardService jobCardService) {
        this.jobCardService = jobCardService;
    }

    @PostMapping
    public ResponseEntity<JobCardResponse> createJobCard(Authentication auth,
                                                         @RequestBody JobCardRequest request) {
        JobCardResponse response = jobCardService.createJobCard(auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<JobCardResponse>> getAll() {
        return ResponseEntity.ok(jobCardService.getAllJobCards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobCardResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobCardService.getJobCardById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobCardResponse>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(jobCardService.getJobCardsByStatus(status));
    }

    /** Assign technician to job card (advisor only) */
    @PutMapping("/{id}/assign-technician")
    public ResponseEntity<JobCardResponse> assignTechnician(@PathVariable Long id,
                                                            @Valid @RequestBody AssignTechnicianRequest request,
                                                            Authentication auth) {
        return ResponseEntity.ok(
                jobCardService.assignTechnician(id, request.getTechnicianId(), auth.getName())
        );
    }

    /** Update job status */
    @PutMapping("/{id}/status")
    public ResponseEntity<JobCardResponse> updateStatus(@PathVariable Long id,
                                                        @Valid @RequestBody JobStatusUpdateRequest request,
                                                        Authentication auth) {
        return ResponseEntity.ok(
                jobCardService.updateStatus(id, request.getStatus(), auth.getName(), request.getNote())
        );
    }

    /** Get status history for a job card */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<ServiceStatusHistoryResponse>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(jobCardService.getStatusHistory(id));
    }
}