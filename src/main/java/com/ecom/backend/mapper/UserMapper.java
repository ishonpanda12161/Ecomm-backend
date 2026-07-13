package com.ecom.backend.mapper;

import com.ecom.backend.model.User;
import com.ecom.backend.payload.SignupDTO;
import com.ecom.backend.payload.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles",ignore = true)
    User signupToUser(SignupDTO signupDTO);

    UserDTO toDTO(User user);
}
