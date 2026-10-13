package com.vst.vehicle_service_tracker.controller;

import com.vst.vehicle_service_tracker.dto.InventoryTransactionResponse;
import com.vst.vehicle_service_tracker.dto.IssuePartsRequest;
import com.vst.vehicle_service_tracker.dto.StockTransactionRequest;
import com.vst.vehicle_service_tracker.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /** Inventory Manager adds stock */
    @PostMapping("/stock-in")
    public ResponseEntity<String> addStock(@Valid @RequestBody StockTransactionRequest request) {
        inventoryService.addStock(request);
        return ResponseEntity.ok("Stock added successfully");
    }

    /** Advisor/Technician issues parts to a job */
    @PostMapping("/issue")
    public ResponseEntity<String> issueParts(@Valid @RequestBody IssuePartsRequest request,
                                             Authentication auth) {
        inventoryService.issuePartToJob(request, auth.getName());
        return ResponseEntity.ok("Parts issued successfully");
    }

    /** View all transactions */
    @GetMapping("/transactions")
    public ResponseEntity<List<InventoryTransactionResponse>> getAll() {
        return ResponseEntity.ok(inventoryService.getAllTransactions());
    }

    @GetMapping("/transactions/job/{jobCardId}")
    public ResponseEntity<List<InventoryTransactionResponse>> getByJob(@PathVariable Long jobCardId) {
        return ResponseEntity.ok(inventoryService.getTransactionsByJob(jobCardId));
    }

    @GetMapping("/transactions/part/{partId}")
    public ResponseEntity<List<InventoryTransactionResponse>> getByPart(@PathVariable Long partId) {
        return ResponseEntity.ok(inventoryService.getTransactionsByPart(partId));
    }
}