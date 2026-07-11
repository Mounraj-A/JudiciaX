package com.courtai.admin.service;

import java.util.Map;

/**
 * Dashboard service for aggregate admin statistics.
 */
public interface AdminDashboardService {
    Map<String, Object> getDashboardStats();
}
