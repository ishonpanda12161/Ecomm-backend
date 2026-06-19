package com.ecom.backend.controller;

import com.ecom.backend.model.Product;
import com.ecom.backend.payload.ProductDTO;
import com.ecom.backend.payload.ProductResponseDTO;
import com.ecom.backend.service.ProductServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponseDTO> getAllProducts(

    )
    {
        return ResponseEntity.ok().body(productService.getAllProducts());
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponseDTO> getProductsByCategory(
            @PathVariable Long categoryId
    ){
        return ResponseEntity.ok().body(productService.getProductsByCategory(categoryId));
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(
            @RequestBody Product product,
            @PathVariable Long categoryId
            )
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(product,categoryId));
    }
}
