package com.example.milksbe.service;

import com.example.milksbe.dto.response.RevenueResponse;
import com.example.milksbe.repository.OrderHeaderRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.IntStream;

@Service
public class RevenueService {
    private static final String PAID_STATUS = "PAID";

    private final OrderHeaderRepository orderHeaderRepository;

    public RevenueService(OrderHeaderRepository orderHeaderRepository) {
        this.orderHeaderRepository = orderHeaderRepository;
    }

    public RevenueResponse getRevenue(LocalDate date) {
        LocalDate targetDate = date == null ? LocalDate.now() : date;

        double dateRevenue = sumRevenue(targetDate, targetDate.plusDays(1));

        LocalDate monthStart = targetDate.withDayOfMonth(1);
        double monthRevenue = sumRevenue(monthStart, monthStart.plusMonths(1));

        LocalDate yearStart = targetDate.withDayOfYear(1);
        double yearRevenue = sumRevenue(yearStart, yearStart.plusYears(1));

        var last7Days = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> targetDate.minusDays(6 - i))
                .map(day -> new RevenueResponse.DailyRevenue(
                        day,
                        sumRevenue(day, day.plusDays(1))
                ))
                .toList();

        return new RevenueResponse(dateRevenue, monthRevenue, yearRevenue, last7Days);
    }

    private double sumRevenue(LocalDate startDate, LocalDate endDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant start = startDate.atStartOfDay(zoneId).toInstant();
        Instant end = endDate.atStartOfDay(zoneId).toInstant();

        return orderHeaderRepository.sumRevenueByStatusAndOrderDateRange(
                PAID_STATUS,
                start,
                end
        );
    }
}