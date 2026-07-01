package cl.techstore.order.service;

import cl.techstore.order.config.OrderQueueConfig;
import cl.techstore.order.model.OrderRequest;
import cl.techstore.order.model.OrderResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {

    private final RabbitTemplate rabbitTemplate;
    private final List<OrderResponse> orders = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public OrderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<OrderResponse> listarOrdenes() {
        return orders;
    }

    public OrderResponse crearOrden(OrderRequest request) {
        Long id = sequence.getAndIncrement();

        OrderResponse response = new OrderResponse(
                id,
                request.getCliente(),
                request.getProductoId(),
                request.getCantidad(),
                request.getDireccion(),
                "ORDEN_CREADA",
                "Orden creada y enviada a cola RabbitMQ para procesamiento asíncrono"
        );

        orders.add(response);

        rabbitTemplate.convertAndSend(OrderQueueConfig.ORDER_CREATED_QUEUE, response);

        return response;
    }
}