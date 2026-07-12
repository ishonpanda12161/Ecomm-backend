package com.ecom.backend.controller;

import com.ecom.backend.payload.AddressDTO;
import com.ecom.backend.service.AddressServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {
    private final AddressServiceImpl addressService;

    @PostMapping("/public/address")
    public ResponseEntity<AddressDTO> createAddress(
        @RequestBody @Valid AddressDTO addressDTO
    )
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(addressDTO));
    }

    @GetMapping("/public/address")
    public ResponseEntity<List<AddressDTO>> getAddress(
    )
    {
        return ResponseEntity.status(HttpStatus.OK).body(addressService.getAllAddresses());
    }

    @GetMapping("/public/address/{id}")
    public ResponseEntity<AddressDTO> getAddressById(
            @PathVariable Long id
    )
    {
        return ResponseEntity.status(HttpStatus.OK).body(addressService.getAddressById(id));
    }

    @PutMapping("/public/address/{id}/update")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable Long id,
            @RequestBody @Valid AddressDTO addressDTO
    )
    {
        return ResponseEntity.status(HttpStatus.OK).body(addressService.updateAddress(id,addressDTO));
    }

    @DeleteMapping("/public/address/{id}")
    public ResponseEntity<String> deleteAddressById(
            @PathVariable Long id
    )
    {
        return ResponseEntity.status(HttpStatus.OK).body(addressService.deleteById(id));
    }
}
