package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.InvoiceResponse;
import com.vst.vehicle_service_tracker.dto.InvoiceSummaryResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse generateInvoice(Long jobCardId, String advisorUsername);
    InvoiceResponse getInvoiceById(Long id);
    InvoiceResponse getInvoiceByJobCard(Long jobCardId);
    List<InvoiceResponse> getAllInvoices();
    List<InvoiceSummaryResponse> getMyInvoices(String customerUsername);
    void recalculateTotals(Long invoiceId);
}