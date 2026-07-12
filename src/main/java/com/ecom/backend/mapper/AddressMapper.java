package com.ecom.backend.mapper;

import com.ecom.backend.model.Address;
import com.ecom.backend.payload.AddressDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {


    AddressDTO toDto(Address address);
    Address toEntity(AddressDTO addressDTO);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "user",ignore = true)
    void updateAddressFromAddressDTO(AddressDTO addressDTO, @MappingTarget Address address);

}
