package com.example.milksbe.repository;

import com.example.milksbe.model.OrderHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Integer> {
    List<OrderHeader> findByCustomerIdOrderByOrderDateDesc(Integer customerId);
    Page<OrderHeader> findAllByOrderByOrderDateDesc(Pageable pageable);

    @Query("""
        select coalesce(sum(o.totalAmount), 0)
        from OrderHeader o
        where o.status = :status
          and o.orderDate >= :start
          and o.orderDate < :end
        """)
    Double sumRevenueByStatusAndOrderDateRange(
            @Param("status") String status,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}