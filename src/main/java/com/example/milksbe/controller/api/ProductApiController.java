package com.example.milksbe.controller.api;

import com.example.milksbe.dto.response.PageResponse;
import com.example.milksbe.dto.response.ProductResponse;
import com.example.milksbe.model.Product;
import com.example.milksbe.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {
    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PageResponse<ProductResponse> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "price,asc") String sort
    ) {
        Sort.Direction direction = sort.toLowerCase().endsWith("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 50),
                Sort.by(direction, "price")
        );

        return PageResponse.from(
                productService.findProducts(keyword, categoryId, pageRequest)
                        .map(this::toResponse)
        );
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Integer id) {
        return toResponse(productService.findById(id));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscount(),
                product.getImageUrl(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
}
