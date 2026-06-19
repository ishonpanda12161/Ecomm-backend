package com.ecom.backend.service;

import com.ecom.backend.model.Product;
import com.ecom.backend.payload.ProductDTO;
import com.ecom.backend.payload.ProductResponseDTO;

public interface ProductService {
    ProductDTO addProduct(Product product, Long categoryId);
    ProductResponseDTO getAllProducts();
    ProductResponseDTO getProductsByCategory(Long categoryId);
}
