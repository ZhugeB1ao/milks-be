package com.example.milksbe.controller.admin;

import com.example.milksbe.dto.response.RevenueResponse;
import com.example.milksbe.service.RevenueService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/revenue")
public class AdminRevenueApiController {
    private final RevenueService revenueService;

    public AdminRevenueApiController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @GetMapping
    public RevenueResponse getRevenue(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return revenueService.getRevenue(date);
    }
}