package com.parnia.ecommerce.payment;

import com.parnia.ecommerce.customer.CustomerResponse;
import com.parnia.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
