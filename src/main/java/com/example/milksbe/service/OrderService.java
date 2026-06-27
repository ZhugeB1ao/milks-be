package com.example.milksbe.service;

import com.example.milksbe.dto.request.CheckoutRequest;
import com.example.milksbe.dto.response.CheckoutResponse;
import com.example.milksbe.model.*;
import com.example.milksbe.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private static final String CUSTOMER_ROLE = "ROLE_CUSTOMER";
    private static final String NEW_STATUS = "NEW";

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderHeaderRepository orderHeaderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderService(
            AccountRepository accountRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            OrderHeaderRepository orderHeaderRepository,
            OrderDetailRepository orderDetailRepository
    ) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderHeaderRepository = orderHeaderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Transactional
    public CheckoutResponse checkout(String email, CheckoutRequest request) {
        Account account = accountRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required"));

        if(!CUSTOMER_ROLE.equals(account.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only customer can checkout");
        }

        Customer customer = customerRepository.findById(account.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer profile not found"));


        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setOrderDate(Instant.now());
        orderHeader.setStatus(NEW_STATUS);
        orderHeader.setCustomer(customer);
        orderHeader.setTotalAmount(0.0);

        OrderHeader savedOrder = orderHeaderRepository.save(orderHeader);

        List<OrderDetail> details = new ArrayList<>();
        List<CheckoutResponse.Item> responseItems = new ArrayList<>();
        double totalAmount = 0.0;

        for(CheckoutRequest.Item item : request.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + item.productId()));

            double price = product.getPrice();
            double discount = product.getDiscount();
            int quantity = item.quantity();
            double lineTotal = price * (1 - discount) * quantity;

            OrderDetail detail = new OrderDetail();
            detail.setOrderHeader(savedOrder);
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setPrice(price);
            detail.setDiscount(discount);

            details.add(detail);
            totalAmount += lineTotal;

            responseItems.add(new CheckoutResponse.Item(
                    product.getId(),
                    product.getDescription(),
                    quantity,
                    price,
                    discount,
                    lineTotal
            ));
        }

        savedOrder.setTotalAmount(totalAmount);
        orderHeaderRepository.save(savedOrder);
        orderDetailRepository.saveAll(details);

        return new CheckoutResponse(
                savedOrder.getId(),
                savedOrder.getOrderDate(),
                savedOrder.getStatus(),
                customer.getId(),
                savedOrder.getTotalAmount(),
                responseItems
        );
    }

    public Page<OrderHeader> findOrders(Pageable pageable) {
        return orderHeaderRepository.findAllByOrderByOrderDateDesc(pageable);
    }

    public OrderHeader findOrderById(Integer id) {
        return orderHeaderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Transactional
    public OrderHeader updateStatus(Integer id, String status) {
        String normalizedStatus = status == null ? "" : status.trim().toUpperCase();

        if (!List.of("NEW", "SHIPPING", "PAID").contains(normalizedStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status");
        }

        OrderHeader order = findOrderById(id);
        order.setStatus(normalizedStatus);
        return orderHeaderRepository.save(order);
    }

    public List<OrderDetail> findOrderDetails(Integer orderId) {
        return orderDetailRepository.findByOrderHeaderId(orderId);
    }
}
