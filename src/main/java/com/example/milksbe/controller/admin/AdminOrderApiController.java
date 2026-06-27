package com.example.milksbe.controller.admin;

import com.example.milksbe.dto.request.OrderStatusRequest;
import com.example.milksbe.dto.response.OrderResponse;
import com.example.milksbe.dto.response.PageResponse;
import com.example.milksbe.model.OrderDetail;
import com.example.milksbe.model.OrderHeader;
import com.example.milksbe.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderApiController {
    private final OrderService orderService;

    public AdminOrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public PageResponse<OrderResponse> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "orderDate")
        );

        return PageResponse.from(
                orderService.findOrders(pageRequest)
                        .map(this::toResponse)
        );
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Integer id) {
        return toResponse(orderService.findOrderById(id));
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(
            @PathVariable Integer id,
            @Valid @RequestBody OrderStatusRequest request
    ) {
        return toResponse(orderService.updateStatus(id, request.status()));
    }

    private OrderResponse toResponse(OrderHeader order) {
        List<OrderResponse.Item> items = orderService.findOrderDetails(order.getId())
                .stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderDate(),
                order.getStatus(),
                toStatusText(order.getStatus()),
                order.getCustomer().getId(),
                order.getCustomer().getAccount().getName(),
                order.getTotalAmount(),
                items
        );
    }

    private OrderResponse.Item toItemResponse(OrderDetail detail) {
        double lineTotal = detail.getPrice()
                * (1 - detail.getDiscount())
                * detail.getQuantity();

        return new OrderResponse.Item(
                detail.getProduct().getId(),
                detail.getProduct().getDescription(),
                detail.getQuantity(),
                detail.getPrice(),
                detail.getDiscount(),
                lineTotal
        );
    }

    private String toStatusText(String status) {
        return switch (status) {
            case "NEW" -> "Mới";
            case "SHIPPING" -> "Đã vận chuyển";
            case "PAID" -> "Đã thanh toán";
            default -> status;
        };
    }
}
