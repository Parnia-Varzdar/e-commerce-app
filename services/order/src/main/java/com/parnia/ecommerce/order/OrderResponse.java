package com.parnia.ecommerce.order;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonInclude(NON_EMPTY)
public record OrderResponse(
        Integer id,
        String reference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String customerId) {

}
