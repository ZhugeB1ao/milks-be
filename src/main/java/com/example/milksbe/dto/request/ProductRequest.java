package com.example.milksbe.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ProductRequest {
        @NotBlank(message = "Description is required")
        @Size(max = 150, message = "Description must not exceed 150 characters")
        private String description;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0")
        private Double price;

        @NotNull(message = "Discount is required")
        @DecimalMin(value = "0.0", message = "Discount must be greater than or equal to 0")
        @DecimalMax(value = "1.0", message = "Discount must be less than or equal to 1")
        private Double discount;

        @NotNull(message = "Category id is required")
        private Integer categoryId;

        private MultipartFile image;

        public String getDescription() {
                return description;
        }

        public Double getPrice() {
                return price;
        }

        public Double getDiscount() {
                return discount;
        }

        public Integer getCategoryId() {
                return categoryId;
        }

        public MultipartFile getImage() {
                return image;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public void setPrice(Double price) {
                this.price = price;
        }

        public void setDiscount(Double discount) {
                this.discount = discount;
        }

        public void setCategoryId(Integer categoryId) {
                this.categoryId = categoryId;
        }

        public void setImage(MultipartFile image) {
                this.image = image;
        }
}
