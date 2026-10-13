package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.entity.Customer;

public interface CustomerService {
    Customer getCustomerByUsername(String username);
    Customer getCustomerById(Long id);
    long getAllCustomersCount();   // ✅ NEW (Day 9)
}