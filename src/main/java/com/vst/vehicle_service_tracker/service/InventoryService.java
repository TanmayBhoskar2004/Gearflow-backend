package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.InventoryTransactionResponse;
import com.vst.vehicle_service_tracker.dto.IssuePartsRequest;
import com.vst.vehicle_service_tracker.dto.StockTransactionRequest;

import java.util.List;

public interface InventoryService {
    void addStock(StockTransactionRequest request);
    void issuePartToJob(IssuePartsRequest request, String issuedByUsername);
    List<InventoryTransactionResponse> getAllTransactions();
    List<InventoryTransactionResponse> getTransactionsByJob(Long jobCardId);
    List<InventoryTransactionResponse> getTransactionsByPart(Long partId);
}