package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.InventoryTransactionResponse;
import com.vst.vehicle_service_tracker.dto.IssuePartsRequest;
import com.vst.vehicle_service_tracker.dto.StockTransactionRequest;
import com.vst.vehicle_service_tracker.entity.*;
import com.vst.vehicle_service_tracker.exception.ResourceNotFoundException;
import com.vst.vehicle_service_tracker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final PartRepository partRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final JobCardRepository jobCardRepository;
    private final JobPartRepository jobPartRepository;

    public InventoryServiceImpl(PartRepository partRepository,
                                InventoryTransactionRepository transactionRepository,
                                JobCardRepository jobCardRepository,
                                JobPartRepository jobPartRepository) {
        this.partRepository = partRepository;
        this.transactionRepository = transactionRepository;
        this.jobCardRepository = jobCardRepository;
        this.jobPartRepository = jobPartRepository;
    }

    @Override
    @Transactional
    public void addStock(StockTransactionRequest request) {
        Part part = partRepository.findById(request.getPartId())
                .orElseThrow(() -> new ResourceNotFoundException("Part not found"));

        part.setStockQuantity(part.getStockQuantity() + request.getQuantity());
        partRepository.save(part);

        InventoryTransaction tx = new InventoryTransaction();
        tx.setPart(part);
        tx.setQuantityChange(request.getQuantity());
        tx.setTransactionType("PURCHASE");
        tx.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void issuePartToJob(IssuePartsRequest request, String issuedByUsername) {
        Part part = partRepository.findById(request.getPartId())
                .orElseThrow(() -> new ResourceNotFoundException("Part not found"));

        JobCard jobCard = jobCardRepository.findById(request.getJobCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Job card not found"));

        if (part.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + part.getStockQuantity());
        }

        // Reduce stock
        part.setStockQuantity(part.getStockQuantity() - request.getQuantity());
        partRepository.save(part);

        // Log the transaction
        InventoryTransaction tx = new InventoryTransaction();
        tx.setPart(part);
        tx.setQuantityChange(-request.getQuantity());
        tx.setTransactionType("ISSUE");
        tx.setJobCard(jobCard);
        tx.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(tx);

        // Link part to job
        JobPart jobPart = new JobPart();
        jobPart.setJobCard(jobCard);
        jobPart.setPart(part);
        jobPart.setQuantityUsed(request.getQuantity());
        jobPart.setUnitPriceAtTime(part.getUnitPrice());
        jobPart.setWarrantyNotes(request.getWarrantyNotes());
        jobPartRepository.save(jobPart);
    }

    @Override
    public List<InventoryTransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionResponse> getTransactionsByJob(Long jobCardId) {
        return transactionRepository.findByJobCardId(jobCardId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionResponse> getTransactionsByPart(Long partId) {
        return transactionRepository.findByPartId(partId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    private InventoryTransactionResponse mapToResponse(InventoryTransaction tx) {
        InventoryTransactionResponse r = new InventoryTransactionResponse();
        r.setId(tx.getId());
        r.setPartId(tx.getPart().getId());
        r.setPartName(tx.getPart().getName());
        r.setQuantityChange(tx.getQuantityChange());
        r.setTransactionType(tx.getTransactionType());
        r.setJobCardId(tx.getJobCard() != null ? tx.getJobCard().getId() : null);
        r.setTransactionDate(tx.getTransactionDate());
        return r;
    }
}