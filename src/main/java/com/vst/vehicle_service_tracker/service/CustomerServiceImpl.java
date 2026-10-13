package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.entity.Customer;
import com.vst.vehicle_service_tracker.entity.User;
import com.vst.vehicle_service_tracker.exception.ResourceNotFoundException;
import com.vst.vehicle_service_tracker.repository.CustomerRepository;
import com.vst.vehicle_service_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Customer getCustomerByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer profile not found for user: " + username));
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Override
    public long getAllCustomersCount() {   // ✅ NEW (Day 9)
        return customerRepository.count();
    }
}