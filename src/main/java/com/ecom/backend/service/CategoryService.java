package com.ecom.backend.service;

import com.ecom.backend.model.Category;
import com.ecom.backend.payload.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long categoryId);
    String updateCategory(Category category);
    void createBulkCategories(List<Category> categories);
}
