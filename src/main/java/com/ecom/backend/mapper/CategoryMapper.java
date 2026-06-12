package com.ecom.backend.mapper;

import com.ecom.backend.model.Category;
import com.ecom.backend.payload.CategoryDTO;
import com.ecom.backend.payload.CategoryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDTO(Category category);
    Category toModel(CategoryDTO categoryDTO);
    List<CategoryDTO> toDTOList(List<Category> categories);
}
