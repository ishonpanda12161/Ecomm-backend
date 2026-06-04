package com.ecom.backend.controller;

import com.ecom.backend.model.Category;
import com.ecom.backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/category")
    public ResponseEntity<List<Category>> getAllCategories()
    {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping("/admin/categoryBulk")
    public ResponseEntity<List<Category>> createBulk(
            @RequestBody List<Category> categories
    )
    {
            categoryService.createBulkCategories(categories);
            return ResponseEntity.ok().body(categories);
    }

    @PostMapping("/admin/category")
    public ResponseEntity<Category> createCategory(
            @RequestBody Category category
    )
    {
        categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PutMapping("/admin/category")
    public ResponseEntity<String> updateCategory(
            @RequestBody Category category
    )
    {
        try{
            String res = categoryService.updateCategory(category);
            return ResponseEntity.ok(res);
        }
        catch (ResponseStatusException e)
        {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

    }

    @DeleteMapping("/admin/category/{categoryId}")
    public ResponseEntity<String> deleteCategory(
            @PathVariable Long categoryId
    )
    {
        try{
            String status = categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok(status);
        }
        catch (ResponseStatusException e)
        {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }


}
