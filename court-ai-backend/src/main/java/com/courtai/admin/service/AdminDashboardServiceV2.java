package com.courtai.admin.service;

import com.courtai.admin.dto.AdminDashboardResponse;

/** Admin dashboard service — returns structured statistics for the admin home screen. */
public interface AdminDashboardServiceV2 {
    AdminDashboardResponse getDashboard();
}
