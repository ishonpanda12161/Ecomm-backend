package com.ecom.backend.controller;

import com.ecom.backend.config.AppConstants;
import com.ecom.backend.payload.ProductDTO;
import com.ecom.backend.payload.ProductResponseDTO;
import com.ecom.backend.service.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponseDTO> getAllProducts(
            @RequestParam(name = "pageNum",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNum,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir
            )
    {
        return ResponseEntity.ok().body(productService.getAllProducts(pageNum,pageSize,sortBy,sortDir));
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponseDTO> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNum",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNum,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir

    ){
        return ResponseEntity.ok().body(productService.getProductsByCategory(categoryId,pageNum,pageSize,sortBy,sortDir));
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponseDTO> getProductsByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "pageNum",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNum,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = AppConstants.SORT_DIR,required = false) String sortDir
    )
    {
        return ResponseEntity.ok().body(productService.getProductsByKeyword(keyword,pageNum,pageSize,sortBy,sortDir));
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(
            @Valid @RequestBody ProductDTO productDTO,
            @PathVariable Long categoryId
            )
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(productDTO,categoryId));
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
        @PathVariable Long productId,
        @Valid @RequestBody ProductDTO productDTO
    )
    {
        return ResponseEntity.ok().body(productService.updateProduct(productId,productDTO));
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        return ResponseEntity.ok().body(productService.updateImage(productId,image));
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId
    )
    {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
