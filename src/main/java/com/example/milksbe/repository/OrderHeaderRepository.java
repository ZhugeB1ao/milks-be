package com.example.milksbe.repository;

import com.example.milksbe.model.OrderHeader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Integer> {
    List<OrderHeader> findByCustomerIdOrderByOrderDateDesc(Integer customerId);
}