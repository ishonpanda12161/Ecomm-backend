package com.ecom.backend.service;

import com.ecom.backend.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long categoryId);
    String updateCategory(Category category);
}
