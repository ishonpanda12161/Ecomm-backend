package com.ecom.backend.payload;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderRequestDTO {

    private Long addressId;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;

}
