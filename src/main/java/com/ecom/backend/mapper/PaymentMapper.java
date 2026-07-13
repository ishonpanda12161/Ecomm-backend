package com.ecom.backend.mapper;

import com.ecom.backend.model.Payment;
import com.ecom.backend.payload.PaymentDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring"
)
public interface PaymentMapper {

    PaymentDTO toDTO(Payment payment);
}
