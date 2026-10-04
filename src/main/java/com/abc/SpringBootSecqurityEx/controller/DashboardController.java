package com.abc.SpringBootSecqurityEx.controller;

import com.abc.SpringBootSecqurityEx.dtos.DashboardDTO;
import com.abc.SpringBootSecqurityEx.entity.User;
import com.abc.SpringBootSecqurityEx.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN', 'PREMIUM_USER', 'EMPLOYEE')")
    public DashboardDTO userDashboard(Authentication authentication) {
        return dashboardService.userDashboard(authentication);
    }

    @GetMapping("/moderator")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public DashboardDTO moderatorDashboard(Authentication authentication) {
        return dashboardService.moderatorDashboard(authentication);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardDTO adminDashboard(Authentication authentication) {
        return dashboardService.adminDashboard(authentication);
    }

    @GetMapping("/premium")
    @PreAuthorize("hasAnyRole('PREMIUM_USER', 'ADMIN')")
    public DashboardDTO premiumDashboard(Authentication authentication) {
        return dashboardService.premiumDashboard(authentication);
    }

    @GetMapping("/data")
    @PreAuthorize("isAuthenticated()")
    public DashboardDTO roleBasedDashboard(Authentication authentication) {
        return dashboardService.roleBasedDashboard(authentication);
    }
}
