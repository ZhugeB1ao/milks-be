package com.example.milksbe.dto.response;

import java.time.LocalDate;
import java.util.List;

public record RevenueResponse(
        Double dateRevenue,
        Double monthRevenue,
        Double yearRevenue,
        List<DailyRevenue> last7Days
) {
    public record DailyRevenue(
            LocalDate date,
            Double revenue
    ) {
    }
}