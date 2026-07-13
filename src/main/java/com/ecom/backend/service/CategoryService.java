package com.ecom.backend.service;


import com.ecom.backend.model.Category;
import com.ecom.backend.payload.CategoryDTO;
import com.ecom.backend.payload.CategoryResponseDTO;
import jakarta.transaction.Transactional;
import java.util.List;

public interface CategoryService {

    CategoryResponseDTO getAllCategories(Integer pageNum, Integer pageSize,String sortBy,String sortDir);

    Category createCategory(CategoryDTO categoryDTO);

    @Transactional
    String deleteCategory(Long categoryId);

    String updateCategory(CategoryDTO categoryDTO);

    void createBulkCategories(List<CategoryDTO> categories);

}
