package com.ecom.backend.repository;

import com.ecom.backend.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByCategoryName(@NotBlank @Size(min = 3,max = 20,message = "Minimum 3 length and cannot exceed 20.") String categoryName);
}

