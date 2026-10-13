package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.InvoiceItemResponse;
import com.vst.vehicle_service_tracker.dto.InvoiceResponse;
import com.vst.vehicle_service_tracker.dto.InvoiceSummaryResponse;
import com.vst.vehicle_service_tracker.entity.*;
import com.vst.vehicle_service_tracker.exception.ResourceNotFoundException;
import com.vst.vehicle_service_tracker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final JobCardRepository jobCardRepository;
    private final JobPartRepository jobPartRepository;
    private final CustomerService customerService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              InvoiceItemRepository invoiceItemRepository,
                              JobCardRepository jobCardRepository,
                              JobPartRepository jobPartRepository,
                              CustomerService customerService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.jobCardRepository = jobCardRepository;
        this.jobPartRepository = jobPartRepository;
        this.customerService = customerService;
    }

    @Override
    @Transactional
    public InvoiceResponse generateInvoice(Long jobCardId, String advisorUsername) {

        JobCard jobCard = jobCardRepository.findById(jobCardId)
                .orElseThrow(() -> new ResourceNotFoundException("Job card not found: " + jobCardId));

        // Check if invoice already exists
        invoiceRepository.findByJobCardId(jobCardId).ifPresent(inv -> {
            throw new RuntimeException("Invoice already exists for this job card");
        });

        // Calculate parts charge from JobParts
        List<JobPart> jobParts = jobPartRepository.findByJobCardId(jobCardId);
        BigDecimal partsCharge = jobParts.stream()
                .map(jp -> jp.getUnitPriceAtTime().multiply(BigDecimal.valueOf(jp.getQuantityUsed())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal labor = jobCard.getLaborCharge() != null ? jobCard.getLaborCharge() : BigDecimal.ZERO;
        BigDecimal other = jobCard.getOtherCharges() != null ? jobCard.getOtherCharges() : BigDecimal.ZERO;
        BigDecimal tax = jobCard.getTax() != null ? jobCard.getTax() : BigDecimal.ZERO;
        BigDecimal discount = jobCard.getDiscount() != null ? jobCard.getDiscount() : BigDecimal.ZERO;

        BigDecimal subtotal = labor.add(partsCharge).add(other);
        BigDecimal total = subtotal.add(tax).subtract(discount);

        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setJobCard(jobCard);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setLaborCharge(labor);
        invoice.setPartsCharge(partsCharge);
        invoice.setOtherCharges(other);
        invoice.setTax(tax);
        invoice.setDiscount(discount);
        invoice.setTotal(total);
        invoice.setPaid(false);
        invoice.setPaymentStatus("UNPAID");
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Create invoice items
        List<InvoiceItem> items = new ArrayList<>();

        // Labor line
        if (labor.compareTo(BigDecimal.ZERO) > 0) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(savedInvoice);
            item.setDescription("Labor Charges");
            item.setQuantity(1);
            item.setUnitPrice(labor);
            item.setTotal(labor);
            items.add(item);
        }

        // Parts lines
        for (JobPart jp : jobParts) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(savedInvoice);
            item.setDescription("Part: " + jp.getPart().getName() + " (" + jp.getPart().getBrand() + ")");
            item.setQuantity(jp.getQuantityUsed());
            item.setUnitPrice(jp.getUnitPriceAtTime());
            item.setTotal(jp.getUnitPriceAtTime().multiply(BigDecimal.valueOf(jp.getQuantityUsed())));
            items.add(item);
        }

        // Other charges
        if (other.compareTo(BigDecimal.ZERO) > 0) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(savedInvoice);
            item.setDescription("Other Charges");
            item.setQuantity(1);
            item.setUnitPrice(other);
            item.setTotal(other);
            items.add(item);
        }

        invoiceItemRepository.saveAll(items);

        return mapToResponse(savedInvoice, items);
    }

    @Override
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice inv = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(id);
        return mapToResponse(inv, items);
    }

    @Override
    public InvoiceResponse getInvoiceByJobCard(Long jobCardId) {
        Invoice inv = invoiceRepository.findByJobCardId(jobCardId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for job card: " + jobCardId));
        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(inv.getId());
        return mapToResponse(inv, items);
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream().map(inv -> {
            List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(inv.getId());
            return mapToResponse(inv, items);
        }).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceSummaryResponse> getMyInvoices(String customerUsername) {
        Customer customer = customerService.getCustomerByUsername(customerUsername);

        return invoiceRepository.findAll().stream()
                .filter(inv -> inv.getJobCard().getServiceRequest().getVehicle().getCustomer()
                        .getId().equals(customer.getId()))
                .map(inv -> {
                    InvoiceSummaryResponse r = new InvoiceSummaryResponse();
                    r.setInvoiceId(inv.getId());
                    r.setRegistrationNumber(inv.getJobCard().getServiceRequest().getVehicle().getRegistrationNumber());
                    r.setTotal(inv.getTotal());
                    r.setPaymentStatus(inv.getPaymentStatus());
                    return r;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recalculateTotals(Long invoiceId) {
        Invoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        List<JobPart> jobParts = jobPartRepository.findByJobCardId(inv.getJobCard().getId());
        BigDecimal partsCharge = jobParts.stream()
                .map(jp -> jp.getUnitPriceAtTime().multiply(BigDecimal.valueOf(jp.getQuantityUsed())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        inv.setPartsCharge(partsCharge);
        BigDecimal subtotal = inv.getLaborCharge().add(partsCharge).add(inv.getOtherCharges());
        inv.setTotal(subtotal.add(inv.getTax()).subtract(inv.getDiscount()));
        invoiceRepository.save(inv);
    }

    private InvoiceResponse mapToResponse(Invoice inv, List<InvoiceItem> items) {
        InvoiceResponse r = new InvoiceResponse();
        r.setId(inv.getId());
        r.setJobCardId(inv.getJobCard().getId());
        r.setRegistrationNumber(inv.getJobCard().getServiceRequest().getVehicle().getRegistrationNumber());
        Customer c = inv.getJobCard().getServiceRequest().getVehicle().getCustomer();
        r.setCustomerName(c.getFirstName() + " " + c.getLastName());
        r.setInvoiceDate(inv.getInvoiceDate());
        r.setLaborCharge(inv.getLaborCharge());
        r.setPartsCharge(inv.getPartsCharge());
        r.setOtherCharges(inv.getOtherCharges());
        r.setTax(inv.getTax());
        r.setDiscount(inv.getDiscount());
        r.setTotal(inv.getTotal());
        r.setPaid(inv.isPaid());
        r.setPaymentStatus(inv.getPaymentStatus());

        List<InvoiceItemResponse> itemResponses = items.stream().map(i -> {
            InvoiceItemResponse ir = new InvoiceItemResponse();
            ir.setId(i.getId());
            ir.setDescription(i.getDescription());
            ir.setQuantity(i.getQuantity());
            ir.setUnitPrice(i.getUnitPrice());
            ir.setTotal(i.getTotal());
            return ir;
        }).collect(Collectors.toList());
        r.setItems(itemResponses);

        return r;
    }
}