package com.parnia.ecommerce.order;

import com.parnia.ecommerce.customer.CustomerClient;
import com.parnia.ecommerce.exception.BusinessException;
import com.parnia.ecommerce.kafka.OrderConfirmation;
import com.parnia.ecommerce.kafka.OrderProducer;
import com.parnia.ecommerce.orderline.OrderLineRequest;
import com.parnia.ecommerce.orderline.OrderLineService;
import com.parnia.ecommerce.payment.PaymentRequest;
import com.parnia.ecommerce.product.ProductClient;
import com.parnia.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderProducer orderProducer;

    @Transactional
    public Integer createOrder(OrderRequest request) {
        var customer = customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        var purchasedProducts = productClient.purchaseProducts(request.products());

        var order = repository.save(mapper.toOrder(request));

        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
//        paymentClient.requestOrderPayment(paymentRequest);

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAllOrders() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}
