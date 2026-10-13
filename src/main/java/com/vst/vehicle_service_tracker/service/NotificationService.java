package com.vst.vehicle_service_tracker.service;

import com.vst.vehicle_service_tracker.dto.NotificationResponse;
import com.vst.vehicle_service_tracker.entity.User;

import java.util.List;

public interface NotificationService {
    void sendNotification(User recipient, String message);
    List<NotificationResponse> getMyNotifications(String username);
    List<NotificationResponse> getMyUnreadNotifications(String username);
    void markAsRead(Long notificationId, String username);
    void markAllAsRead(String username);
}