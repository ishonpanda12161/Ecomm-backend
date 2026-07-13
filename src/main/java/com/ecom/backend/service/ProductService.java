package com.ecom.backend.service;

import com.ecom.backend.payload.ProductDTO;
import com.ecom.backend.payload.ProductResponseDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);
    @Transactional(readOnly = true)
    ProductResponseDTO getAllProducts(Integer pageNum, Integer pageSize, String sortBy, String sortDir);
    ProductResponseDTO getProductsByCategory(Long categoryId,Integer pageNum, Integer pageSize, String sortBy, String sortDir);
    ProductResponseDTO getProductsByKeyword(String keyword,Integer pageNum, Integer pageSize, String sortBy, String sortDir);
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    @Transactional
    ProductDTO deleteProduct(Long productId);
    ProductDTO updateImage(Long productId, MultipartFile image) throws IOException;
}
