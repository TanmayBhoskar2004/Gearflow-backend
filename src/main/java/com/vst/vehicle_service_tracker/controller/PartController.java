package com.vst.vehicle_service_tracker.controller;

import com.vst.vehicle_service_tracker.dto.PartRequest;
import com.vst.vehicle_service_tracker.dto.PartResponse;
import com.vst.vehicle_service_tracker.service.PartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @PostMapping
    public ResponseEntity<PartResponse> create(@Valid @RequestBody PartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partService.createPart(request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PartResponse>> getAll() {
        return ResponseEntity.ok(partService.getAllParts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(partService.getPartById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody PartRequest request) {
        return ResponseEntity.ok(partService.updatePart(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<PartResponse>> getLowStock(@RequestParam(defaultValue = "5") int threshold) {
        return ResponseEntity.ok(partService.getLowStockParts(threshold));
    }
}