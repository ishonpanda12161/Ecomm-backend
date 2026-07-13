package com.ecom.backend.service;

import com.ecom.backend.payload.AddressDTO;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddressService {
    @Transactional
    AddressDTO createAddress(AddressDTO addressDTO);
    @Transactional(readOnly = true)
    List<AddressDTO> getAllAddresses();
    @Transactional(readOnly = true)
    AddressDTO getAddressById(Long id);
    @Transactional
    AddressDTO updateAddress(Long id, @Valid AddressDTO addressDTO);
    @Transactional
    String deleteById(Long id);
}
