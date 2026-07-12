package com.ecom.backend.repository;

import com.ecom.backend.model.Address;
import com.ecom.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    Optional<Address> findByIdAndUser(Long id, User user);
}
