package com.ecom.backend.service;

import com.ecom.backend.exceptions.GenericAPIException;
import com.ecom.backend.exceptions.ResourceAlreadyExistsException;
import com.ecom.backend.exceptions.ResourceNotFoundException;
import com.ecom.backend.mapper.CategoryMapper;
import com.ecom.backend.model.Category;
import com.ecom.backend.payload.CategoryResponseDTO;
import com.ecom.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryResponseDTO getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty())
        {
            throw new GenericAPIException("No categories present.");
        }
        return new CategoryResponseDTO(categoryMapper.toDTOList(categories));
    }

    @Override
    public void createBulkCategories(List<Category> categories) {
        categoryRepository.saveAll(categories);
    }

    @Override
    public void createCategory(Category category) {
        Category temp = categoryRepository.findByCategoryName(category.getCategoryName());

        if(temp!=null)
        {
            throw new ResourceAlreadyExistsException("Category",category.getCategoryId());
        }
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","ID",categoryId));

        categoryRepository.delete(category);
        return category.getCategoryName() + "  deleted";
    }

    @Override
    public String updateCategory(Category category) {
        Category cat = categoryRepository.findById(category.getCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Category","ID", category.getCategoryId()));
        cat.setCategoryName(category.getCategoryName());
        categoryRepository.save(cat);
        return "Updated!";
    }
}
