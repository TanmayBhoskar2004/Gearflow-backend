package com.vst.vehicle_service_tracker.controller;

import com.vst.vehicle_service_tracker.dto.JobCardResponse;
import com.vst.vehicle_service_tracker.dto.TechnicianResponse;
import com.vst.vehicle_service_tracker.service.JobCardService;
import com.vst.vehicle_service_tracker.service.TechnicianService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    private final TechnicianService technicianService;
    private final JobCardService jobCardService;

    public TechnicianController(TechnicianService technicianService,
                                JobCardService jobCardService) {
        this.technicianService = technicianService;
        this.jobCardService = jobCardService;
    }

    /** Advisor views all technicians */
    @GetMapping("/all")
    public ResponseEntity<List<TechnicianResponse>> getAll() {
        return ResponseEntity.ok(technicianService.getAllTechnicians());
    }

    /** Advisor views available technicians */
    @GetMapping("/available")
    public ResponseEntity<List<TechnicianResponse>> getAvailable() {
        return ResponseEntity.ok(technicianService.getAvailableTechnicians());
    }

    /** Technician views their own profile */
    @GetMapping("/me")
    public ResponseEntity<TechnicianResponse> getMe(Authentication auth) {
        return ResponseEntity.ok(technicianService.getTechnicianByUsername(auth.getName()));
    }

    /** Technician views jobs assigned to them */
    @GetMapping("/my-jobs")
    public ResponseEntity<List<JobCardResponse>> getMyJobs(Authentication auth) {
        return ResponseEntity.ok(jobCardService.getMyAssignedJobs(auth.getName()));
    }
}