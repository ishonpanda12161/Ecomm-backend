package com.ecom.backend.service;

import com.ecom.backend.exceptions.GenericAPIException;
import com.ecom.backend.exceptions.ResourceAlreadyExistsException;
import com.ecom.backend.exceptions.ResourceNotFoundException;
import com.ecom.backend.mapper.CategoryMapper;
import com.ecom.backend.model.Category;
import com.ecom.backend.payload.CategoryDTO;
import com.ecom.backend.payload.CategoryResponseDTO;
import com.ecom.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public CategoryResponseDTO getAllCategories(Integer pageNum, Integer pageSize,String sortBy,String sortDir) {

        Pageable page = PageRequest.of(pageNum,pageSize,Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Order.asc(sortBy) : Sort.Order.desc(sortBy)));
        Page<Category> categories = categoryRepository.findAll(page);

        List<CategoryDTO> list = categoryMapper.toDTOList(categories.getContent());

        return new CategoryResponseDTO(list,categories.getNumber(),categories.getSize(),categories.getTotalElements(),categories.getTotalPages(),categories.isLast());

    }

    @Override
    public void createBulkCategories(List<CategoryDTO> categories)
    {
        if(categories.isEmpty())
        {
            throw new GenericAPIException("Nothing to Create. List Empty");
        }
        List<Category> list = categories.stream().
                map(categoryMapper::toModel)
                        .toList();

        categoryRepository.saveAll(list);
    }

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {

        Category category = categoryMapper.toModel(categoryDTO);
        Category temp = categoryRepository.findByCategoryName(category.getCategoryName());
        if(temp!=null)
        {
            throw new ResourceAlreadyExistsException("Category",category.getCategoryId());
        }
        return categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","ID",categoryId));

        categoryRepository.delete(category);
        return category.getCategoryName() + "  deleted";
    }

    @Override
    public String updateCategory(CategoryDTO categoryDTO) {

        Category cat = categoryRepository.findById(categoryDTO.getCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Category","ID", categoryDTO.getCategoryId()));
        cat.setCategoryName(categoryDTO.getCategoryName());
        categoryRepository.save(cat);
        return "Updated!";
    }
}
