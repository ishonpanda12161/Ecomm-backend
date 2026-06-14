package com.ecom.backend.controller;


import com.ecom.backend.payload.CategoryDTO;
import com.ecom.backend.payload.CategoryResponseDTO;
import com.ecom.backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/category")
    public ResponseEntity<CategoryResponseDTO> getAllCategories(
            @RequestParam(name = "pageNum",defaultValue = "0") Integer pageNum,
            @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = "categoryId") String sortBy,
            @RequestParam(name = "sortDir",defaultValue = "asc") String sortDir

    )
    {
        return ResponseEntity.ok(categoryService.getAllCategories(pageNum,pageSize,sortBy,sortDir));
    }

    @PostMapping("/admin/categoryBulk")
    public ResponseEntity<String> createBulk(
            @RequestBody List<CategoryDTO> categories
    )
    {
            categoryService.createBulkCategories(categories);
            return ResponseEntity.ok().body("Created");
    }

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryDTO> createCategory(
           @Valid @RequestBody CategoryDTO categoryDTO
    )
    {
        categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO);
    }

    @PutMapping("/admin/category")
    public ResponseEntity<String> updateCategory(
            @RequestBody CategoryDTO categoryDTO
    )
    {
        String res = categoryService.updateCategory(categoryDTO);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/admin/category/{categoryId}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable Long categoryId
    )
    {
        String status = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(status);
    }


}
