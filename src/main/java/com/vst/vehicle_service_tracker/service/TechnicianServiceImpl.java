package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.TechnicianResponse;
import com.vst.vehicle_service_tracker.entity.Technician;
import com.vst.vehicle_service_tracker.exception.ResourceNotFoundException;
import com.vst.vehicle_service_tracker.repository.TechnicianRepository;
import com.vst.vehicle_service_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final UserRepository userRepository;

    public TechnicianServiceImpl(TechnicianRepository technicianRepository,
                                 UserRepository userRepository) {
        this.technicianRepository = technicianRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<TechnicianResponse> getAllTechnicians() {
        return technicianRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<TechnicianResponse> getAvailableTechnicians() {
        return technicianRepository.findByAvailabilityTrue().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public TechnicianResponse getTechnicianByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Technician tech = technicianRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Technician profile not found"));
        return mapToResponse(tech);
    }

    private TechnicianResponse mapToResponse(Technician t) {
        TechnicianResponse r = new TechnicianResponse();
        r.setId(t.getId());
        r.setUsername(t.getUser().getUsername());
        r.setEmail(t.getUser().getEmail());
        r.setSpecialization(t.getSpecialization());
        r.setAvailability(t.isAvailability());
        return r;
    }
}