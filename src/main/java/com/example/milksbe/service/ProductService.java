package com.example.milksbe.service;

import com.example.milksbe.model.Product;
import com.example.milksbe.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> findProducts(String keyword, Integer categoryId, Pageable pageable) {
        String normalizedKeyword = keyword == null || keyword.isBlank()
                ? null
                : keyword.trim();

        return productRepository.searchProducts(normalizedKeyword, categoryId, pageable);
    }

    public Product findById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
