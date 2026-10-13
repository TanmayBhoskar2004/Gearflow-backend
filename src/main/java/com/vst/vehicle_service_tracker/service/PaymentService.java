package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.PaymentRequest;
import com.vst.vehicle_service_tracker.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse recordPayment(PaymentRequest request, String customerUsername);
    List<PaymentResponse> getPaymentsByInvoice(Long invoiceId);
    PaymentResponse getPaymentById(Long id);
}