package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.PartRequest;
import com.vst.vehicle_service_tracker.dto.PartResponse;
import com.vst.vehicle_service_tracker.entity.Part;
import com.vst.vehicle_service_tracker.exception.ResourceNotFoundException;
import com.vst.vehicle_service_tracker.repository.PartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;

    public PartServiceImpl(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Override
    @Transactional
    public PartResponse createPart(PartRequest request) {
        Part part = new Part();
        part.setName(request.getName());
        part.setBrand(request.getBrand());
        part.setUnitPrice(request.getUnitPrice());
        part.setWarrantyMonths(request.getWarrantyMonths());
        part.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        return mapToResponse(partRepository.save(part));
    }

    @Override
    @Transactional
    public PartResponse updatePart(Long id, PartRequest request) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found: " + id));

        part.setName(request.getName());
        part.setBrand(request.getBrand());
        part.setUnitPrice(request.getUnitPrice());
        part.setWarrantyMonths(request.getWarrantyMonths());
        // Stock quantity is updated only through stock transactions, not directly here
        return mapToResponse(partRepository.save(part));
    }

    @Override
    public PartResponse getPartById(Long id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found: " + id));
        return mapToResponse(part);
    }

    @Override
    public List<PartResponse> getAllParts() {
        return partRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<PartResponse> getLowStockParts(int threshold) {
        return partRepository.findByStockQuantityLessThan(threshold).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePart(Long id) {
        if (!partRepository.existsById(id)) {
            throw new ResourceNotFoundException("Part not found: " + id);
        }
        partRepository.deleteById(id);
    }

    private PartResponse mapToResponse(Part part) {
        PartResponse r = new PartResponse();
        r.setId(part.getId());
        r.setName(part.getName());
        r.setBrand(part.getBrand());
        r.setUnitPrice(part.getUnitPrice());
        r.setWarrantyMonths(part.getWarrantyMonths());
        r.setStockQuantity(part.getStockQuantity());
        return r;
    }
}