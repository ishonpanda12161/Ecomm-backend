package com.ecom.backend.service;

import com.ecom.backend.model.Category;
import com.ecom.backend.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createBulkCategories(List<Category> categories) {
        categoryRepository.saveAll(categories);
    }

    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found"));

        categoryRepository.delete(category);
        return category.getCategoryName() + "  deleted";
    }

    @Override
    public String updateCategory(Category category) {
        Category cat = categoryRepository.findById(category.getCategoryId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category with category id : "+category.getCategoryId()+" not found"));
        cat.setCategoryName(category.getCategoryName());
        categoryRepository.save(cat);
        return "Updated!";
    }
}
