package com.example.milksbe.service;

import com.example.milksbe.dto.request.ProductRequest;
import com.example.milksbe.model.Category;
import com.example.milksbe.model.Product;
import com.example.milksbe.repository.CategoryRepository;
import com.example.milksbe.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            FileStorageService fileStorageService
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    public Product create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        String imageUrl = fileStorageService.saveProductImage(request.getImage());

        Product product = new Product();
        product.setDescription(request.getDescription().trim());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        product.setImageUrl(imageUrl);
        product.setCategory(category);

        return productRepository.save(product);
    }

    public Product update(Integer id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        product.setDescription(request.getDescription().trim());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        product.setCategory(category);

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            product.setImageUrl(fileStorageService.saveProductImage(request.getImage()));
        }

        return productRepository.save(product);
    }

    public void delete(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        productRepository.deleteById(id);
    }

    public Page<Product> findProducts(String keyword, Integer categoryId, Pageable pageable) {
        String normalizedKeyword = keyword == null || keyword.isBlank()
                ? null
                : keyword.trim();

        return productRepository.searchProducts(normalizedKeyword, categoryId, pageable);
    }

    public Product findById(Integer id) {
        return productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
