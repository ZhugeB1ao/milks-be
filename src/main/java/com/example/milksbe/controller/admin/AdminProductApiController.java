package com.example.milksbe.controller.admin;

import com.example.milksbe.dto.request.ProductRequest;
import com.example.milksbe.dto.response.PageResponse;
import com.example.milksbe.dto.response.ProductResponse;
import com.example.milksbe.model.Product;
import com.example.milksbe.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductApiController {
    private final ProductService productService;

    public AdminProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PageResponse<ProductResponse> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.ASC, "id")
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @ModelAttribute ProductRequest request) {
        return toResponse(productService.create(request));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequest request
    ) {
        return toResponse(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Integer id) {
        productService.delete(id);
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
