package com.parnia.ecommerce.kafka;

import com.parnia.ecommerce.customer.CustomerResponse;
import com.parnia.ecommerce.order.PaymentMethod;
import com.parnia.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
