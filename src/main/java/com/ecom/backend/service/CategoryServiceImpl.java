package com.ecom.backend.service;

import com.ecom.backend.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final List<Category> categories = new ArrayList<>();

    private Long id = 1L;

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        category.setCategoryId(id++);
        categories.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categories.stream()
                .filter( c -> c.getCategoryId()
                        .equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found"));

        categories.remove(category);
        return category.getCategoryName() + "  deleted";
    }

    @Override
    public String updateCategory(Category category) {
        Category cat = categories.stream()
                .filter(c-> c.getCategoryId()
                        .equals(category.getCategoryId()))
                .findFirst()
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category with category id : "+category.getCategoryId()+" not found"));
        cat.setCategoryName(category.getCategoryName());
        return "Updated!";
    }
}
