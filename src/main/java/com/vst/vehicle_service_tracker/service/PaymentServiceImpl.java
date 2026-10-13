package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.PaymentRequest;
import com.vst.vehicle_service_tracker.dto.PaymentResponse;
import com.vst.vehicle_service_tracker.entity.Customer;
import com.vst.vehicle_service_tracker.entity.Invoice;
import com.vst.vehicle_service_tracker.entity.Payment;
import com.vst.vehicle_service_tracker.exception.ResourceNotFoundException;
import com.vst.vehicle_service_tracker.repository.InvoiceRepository;
import com.vst.vehicle_service_tracker.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              InvoiceRepository invoiceRepository,
                              CustomerService customerService) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.customerService = customerService;
    }

    @Override
    @Transactional
    public PaymentResponse recordPayment(PaymentRequest request, String customerUsername) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        // Check that this invoice belongs to the customer
        Customer customer = customerService.getCustomerByUsername(customerUsername);
        if (!invoice.getJobCard().getServiceRequest().getVehicle().getCustomer()
                .getId().equals(customer.getId())) {
            throw new RuntimeException("You are not authorized to pay this invoice");
        }

        if (invoice.isPaid()) {
            throw new RuntimeException("Invoice is already paid");
        }

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(invoice.getTotal()) < 0) {
            throw new RuntimeException("Payment amount is less than total. Required: " + invoice.getTotal());
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setMethod(request.getMethod() != null ? request.getMethod() : "CASH");
        payment.setReference(request.getReference());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("COMPLETED");
        Payment saved = paymentRepository.save(payment);

        // Update invoice
        invoice.setPaid(true);
        invoice.setPaymentStatus("PAID");
        invoiceRepository.save(invoice);

        return mapToResponse(saved);
    }

    @Override
    public List<PaymentResponse> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment p = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        return mapToResponse(p);
    }

    private PaymentResponse mapToResponse(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setInvoiceId(p.getInvoice().getId());
        r.setAmount(p.getAmount());
        r.setMethod(p.getMethod());
        r.setReference(p.getReference());
        r.setPaymentDate(p.getPaymentDate());
        r.setStatus(p.getStatus());
        return r;
    }
}