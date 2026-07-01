package cl.techstore.order.model;

public class OrderResponse {

    private Long id;
    private String cliente;
    private Long productoId;
    private Integer cantidad;
    private String direccion;
    private String estado;
    private String mensaje;

    public OrderResponse() {
    }

    public OrderResponse(Long id, String cliente, Long productoId, Integer cantidad, String direccion, String estado, String mensaje) {
        this.id = id;
        this.cliente = cliente;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.direccion = direccion;
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public Long getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public Long getProductoId() {
        return productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }
}