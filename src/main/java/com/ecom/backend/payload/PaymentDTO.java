package com.ecom.backend.payload;


import lombok.Data;

@Data
public class PaymentDTO {

    private Long id;
    private String paymentMethod;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;

}
