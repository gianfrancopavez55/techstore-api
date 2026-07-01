package cl.techstore.order.controller;

import cl.techstore.order.model.OrderRequest;
import cl.techstore.order.model.OrderResponse;
import cl.techstore.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> listarOrdenes() {
        return ResponseEntity.ok(orderService.listarOrdenes());
    }

    @PostMapping
    public ResponseEntity<OrderResponse> crearOrden(@RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.crearOrden(request));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("order-service UP");
    }
}
