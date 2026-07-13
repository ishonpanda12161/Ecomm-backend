package com.ecom.backend.service;

import com.ecom.backend.config.AuthUtil;
import com.ecom.backend.exceptions.ResourceNotFoundException;
import com.ecom.backend.mapper.AddressMapper;
import com.ecom.backend.model.Address;
import com.ecom.backend.model.User;
import com.ecom.backend.payload.AddressDTO;
import com.ecom.backend.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final AuthUtil authUtil;

    @Transactional
    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        Address address = addressMapper.toEntity(addressDTO);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);

        return addressMapper.toDto(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    @Override
    public List<AddressDTO> getAllAddresses() {
        User user = authUtil.loggedInUser();
        List<AddressDTO> addressDTOS = user.getAddresses().stream().map(addressMapper::toDto).toList();
        return addressDTOS;
    }

    @Transactional(readOnly = true)
    @Override
    public AddressDTO getAddressById(Long id) {
        User user = authUtil.loggedInUser();
        Address address = addressRepository.findByIdAndUser(id,user)
                .orElseThrow(()-> new ResourceNotFoundException("Addres","id",id));
        return addressMapper.toDto(address);
    }

    @Transactional
    @Override
    public AddressDTO updateAddress(Long id, AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        Address address = addressRepository.findByIdAndUser(id,user)
                .orElseThrow(()-> new ResourceNotFoundException("Address","id",id));
        addressMapper.updateAddressFromAddressDTO(addressDTO,address);
        return addressMapper.toDto(addressRepository.save(address));
    }

    @Transactional
    @Override
    public String deleteById(Long id) {
        User user = authUtil.loggedInUser();
        Address address = addressRepository.findByIdAndUser(id,user)
                .orElseThrow(()-> new ResourceNotFoundException("Address","id",id));
        addressRepository.delete(address);
        return "Deleted";
    }

}
