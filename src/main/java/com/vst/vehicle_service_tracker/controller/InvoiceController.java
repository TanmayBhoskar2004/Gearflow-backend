package com.vst.vehicle_service_tracker.controller;

import com.vst.vehicle_service_tracker.dto.InvoiceResponse;
import com.vst.vehicle_service_tracker.dto.InvoiceSummaryResponse;
import com.vst.vehicle_service_tracker.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /** Advisor generates invoice from job card */
    @PostMapping("/generate/{jobCardId}")
    public ResponseEntity<InvoiceResponse> generate(@PathVariable Long jobCardId,
                                                    Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.generateInvoice(jobCardId, auth.getName()));
    }

    /** View any invoice (Advisor/Admin or owner Customer) */
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    /** Get invoice by job card */
    @GetMapping("/job-card/{jobCardId}")
    public ResponseEntity<InvoiceResponse> getByJobCard(@PathVariable Long jobCardId) {
        return ResponseEntity.ok(invoiceService.getInvoiceByJobCard(jobCardId));
    }

    /** Advisor/Admin view all invoices */
    @GetMapping("/all")
    public ResponseEntity<List<InvoiceResponse>> getAll() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    /** Customer views own invoices */
    @GetMapping("/my")
    public ResponseEntity<List<InvoiceSummaryResponse>> getMy(Authentication auth) {
        return ResponseEntity.ok(invoiceService.getMyInvoices(auth.getName()));
    }
}