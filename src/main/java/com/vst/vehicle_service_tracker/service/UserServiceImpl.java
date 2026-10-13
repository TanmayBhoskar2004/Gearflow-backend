package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.RegisterRequest;
import com.vst.vehicle_service_tracker.entity.Customer;
import com.vst.vehicle_service_tracker.entity.Role;
import com.vst.vehicle_service_tracker.entity.User;
import com.vst.vehicle_service_tracker.repository.CustomerRepository;
import com.vst.vehicle_service_tracker.repository.RoleRepository;
import com.vst.vehicle_service_tracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        String roleName = request.getRole() != null ? request.getRole() : "CUSTOMER";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRole(role);
        userRepository.save(user);

        if (roleName.equalsIgnoreCase("CUSTOMER")) {
            Customer customer = new Customer();
            customer.setUser(user);
            customer.setFirstName(request.getFirstName());
            customer.setLastName(request.getLastName());
            customer.setPhone(request.getPhone());
            customer.setAddress(request.getAddress());
            customerRepository.save(customer);
        }
    }
}