package com.ecom.backend.payload;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDTO {

    private Long id;
    private String email;
    private List<OrderItemDTO> orderItemsDTOList;
    private LocalDate date;
    private Double Total;
    private String status;
    private Long addressId;
    private PaymentDTO paymentDTO;
}
