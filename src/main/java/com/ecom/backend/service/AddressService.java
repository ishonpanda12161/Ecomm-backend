package com.ecom.backend.service;

import com.ecom.backend.payload.AddressDTO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO);
    List<AddressDTO> getAllAddresses();
    AddressDTO getAddressById(Long id);
    @Transactional
    AddressDTO updateAddress(Long id, @Valid AddressDTO addressDTO);
    String deleteById(Long id);
}
