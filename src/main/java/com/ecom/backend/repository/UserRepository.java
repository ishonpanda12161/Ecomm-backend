package com.ecom.backend.repository;

import com.ecom.backend.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String username);
    User findByUsername(String username);
    boolean existsByUsername(@NotBlank @Size(min = 5,message = "Must contain at least 3 characters.") String username);
}
