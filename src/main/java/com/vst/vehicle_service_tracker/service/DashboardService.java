package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.*;

public interface DashboardService {
    CustomerDashboardResponse getCustomerDashboard(String username);
    AdvisorDashboardResponse getAdvisorDashboard();
    TechnicianDashboardResponse getTechnicianDashboard(String username);
    InventoryDashboardResponse getInventoryDashboard();
    AdminDashboardResponse getAdminDashboard();
}