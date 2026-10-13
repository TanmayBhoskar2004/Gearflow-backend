package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.*;
import com.vst.vehicle_service_tracker.entity.*;
import com.vst.vehicle_service_tracker.exception.ResourceNotFoundException;
import com.vst.vehicle_service_tracker.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final CustomerService customerService;
    private final VehicleRepository vehicleRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final JobCardRepository jobCardRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationRepository notificationRepository;
    private final PartRepository partRepository;
    private final UserRepository userRepository;
    private final TechnicianRepository technicianRepository;
    private final JobTechnicianRepository jobTechnicianRepository;

    public DashboardServiceImpl(CustomerService customerService,
                                VehicleRepository vehicleRepository,
                                ServiceRequestRepository serviceRequestRepository,
                                JobCardRepository jobCardRepository,
                                InvoiceRepository invoiceRepository,
                                NotificationRepository notificationRepository,
                                PartRepository partRepository,
                                UserRepository userRepository,
                                TechnicianRepository technicianRepository,
                                JobTechnicianRepository jobTechnicianRepository) {
        this.customerService = customerService;
        this.vehicleRepository = vehicleRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.jobCardRepository = jobCardRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationRepository = notificationRepository;
        this.partRepository = partRepository;
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
        this.jobTechnicianRepository = jobTechnicianRepository;
    }

    // ================= CUSTOMER DASHBOARD =================
    @Override
    public CustomerDashboardResponse getCustomerDashboard(String username) {
        Customer customer = customerService.getCustomerByUsername(username);
        CustomerDashboardResponse r = new CustomerDashboardResponse();

        List<Vehicle> myVehicles = vehicleRepository.findByCustomerId(customer.getId());
        r.setVehicleCount(myVehicles.size());

        Set<Long> myVehicleIds = myVehicles.stream().map(Vehicle::getId).collect(Collectors.toSet());
        List<ServiceRequest> myRequests = serviceRequestRepository.findAll().stream()
                .filter(sr -> myVehicleIds.contains(sr.getVehicle().getId()))
                .collect(Collectors.toList());

        Set<Long> myRequestIds = myRequests.stream().map(ServiceRequest::getId).collect(Collectors.toSet());
        List<JobCard> myJobs = jobCardRepository.findAll().stream()
                .filter(jc -> myRequestIds.contains(jc.getServiceRequest().getId()))
                .collect(Collectors.toList());

        long active = myJobs.stream()
                .filter(jc -> jc.getStatus() != JobStatus.DELIVERED)
                .count();
        r.setActiveServiceCount(active);
        r.setCompletedServiceCount(myJobs.stream()
                .filter(jc -> jc.getStatus() == JobStatus.DELIVERED)
                .count());

        List<Invoice> myInvoices = invoiceRepository.findAll().stream()
                .filter(inv -> myJobs.stream()
                        .anyMatch(jc -> jc.getId().equals(inv.getJobCard().getId())))
                .collect(Collectors.toList());

        r.setUnpaidInvoiceCount(myInvoices.stream().filter(i -> !i.isPaid()).count());
        BigDecimal outstanding = myInvoices.stream()
                .filter(i -> !i.isPaid())
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        r.setOutstandingAmount(outstanding);

        r.setUnreadNotificationCount(
                notificationRepository.findByUserIdAndIsReadFalse(customer.getUser().getId()).size()
        );
        return r;
    }

    // ================= ADVISOR DASHBOARD =================
    @Override
    public AdvisorDashboardResponse getAdvisorDashboard() {
        AdvisorDashboardResponse r = new AdvisorDashboardResponse();
        r.setTotalCustomers(customerService.getAllCustomersCount());
        r.setTotalVehicles(vehicleRepository.count());

        r.setPendingServiceRequests(
                serviceRequestRepository.findAll().stream()
                        .filter(sr -> "PENDING".equalsIgnoreCase(sr.getStatus()))
                        .count()
        );

        List<JobCard> jobs = jobCardRepository.findAll();
        r.setTotalJobCards(jobs.size());

        Map<String, Long> byStatus = new HashMap<>();
        for (JobStatus s : JobStatus.values()) {
            byStatus.put(s.name(), jobs.stream().filter(j -> j.getStatus() == s).count());
        }
        r.setJobsByStatus(byStatus);

        r.setUnpaidInvoices(invoiceRepository.findAll().stream().filter(i -> !i.isPaid()).count());
        r.setLowStockParts(partRepository.findByStockQuantityLessThan(5).size());
        return r;
    }

    // ================= TECHNICIAN DASHBOARD =================
    @Override
    public TechnicianDashboardResponse getTechnicianDashboard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Technician tech = technicianRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Technician profile not found"));

        List<JobCard> myJobs = jobTechnicianRepository.findByTechnicianId(tech.getId()).stream()
                .map(JobTechnician::getJobCard)
                .collect(Collectors.toList());

        TechnicianDashboardResponse r = new TechnicianDashboardResponse();
        r.setAssignedJobCount(myJobs.size());

        Map<String, Long> byStatus = new HashMap<>();
        for (JobStatus s : JobStatus.values()) {
            byStatus.put(s.name(), myJobs.stream().filter(j -> j.getStatus() == s).count());
        }
        r.setMyJobsByStatus(byStatus);
        return r;
    }

    // ================= INVENTORY DASHBOARD =================
    @Override
    public InventoryDashboardResponse getInventoryDashboard() {
        InventoryDashboardResponse r = new InventoryDashboardResponse();
        List<Part> allParts = partRepository.findAll();

        r.setTotalParts(allParts.size());
        r.setTotalStockQuantity(allParts.stream().mapToLong(Part::getStockQuantity).sum());
        BigDecimal totalValue = allParts.stream()
                .map(p -> p.getUnitPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        r.setTotalInventoryValue(totalValue);

        List<Part> lowStock = partRepository.findByStockQuantityLessThan(5);
        r.setLowStockCount(lowStock.size());
        r.setLowStockParts(lowStock.stream().map(p -> {
            PartResponse pr = new PartResponse();
            pr.setId(p.getId());
            pr.setName(p.getName());
            pr.setBrand(p.getBrand());
            pr.setUnitPrice(p.getUnitPrice());
            pr.setWarrantyMonths(p.getWarrantyMonths());
            pr.setStockQuantity(p.getStockQuantity());
            return pr;
        }).collect(Collectors.toList()));
        return r;
    }

    // ================= ADMIN DASHBOARD =================
    @Override
    public AdminDashboardResponse getAdminDashboard() {
        AdminDashboardResponse r = new AdminDashboardResponse();

        List<User> users = userRepository.findAll();
        r.setTotalUsers(users.size());

        Map<String, Long> byRole = new HashMap<>();
        for (User u : users) {
            String role = u.getRole().getName();
            byRole.put(role, byRole.getOrDefault(role, 0L) + 1);
        }
        r.setUsersByRole(byRole);

        r.setTotalCustomers(byRole.getOrDefault("CUSTOMER", 0L));
        r.setTotalTechnicians(byRole.getOrDefault("TECHNICIAN", 0L));
        r.setTotalVehicles(vehicleRepository.count());
        r.setTotalJobCards(jobCardRepository.count());

        List<Invoice> invoices = invoiceRepository.findAll();
        r.setTotalRevenue(invoices.stream()
                .filter(Invoice::isPaid)
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        r.setTotalOutstanding(invoices.stream()
                .filter(i -> !i.isPaid())
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        r.setLowStockParts(partRepository.findByStockQuantityLessThan(5).size());
        return r;
    }
}