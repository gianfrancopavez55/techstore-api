package techstore_api.service;

import org.springframework.stereotype.Service;
import techstore_api.model.Producto;
import techstore_api.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final AuditSqsService auditSqsService;

    public ProductoService(ProductoRepository productoRepository, AuditSqsService auditSqsService) {
        this.productoRepository = productoRepository;
        this.auditSqsService = auditSqsService;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.orElse(null);
    }

    public Producto crear(Producto producto) {
        validarProducto(producto);

        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }

        Producto guardado = productoRepository.save(producto);
        auditSqsService.enviarEventoAuditoria("CREAR", guardado);
        return guardado;
    }

    public Producto actualizar(Long id, Producto productoActualizado) {
        Optional<Producto> productoOptional = productoRepository.findById(id);

        if (productoOptional.isEmpty()) {
            return null;
        }

        validarProducto(productoActualizado);

        Producto producto = productoOptional.get();
        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setPrecio(productoActualizado.getPrecio());
        producto.setStock(productoActualizado.getStock());
        producto.setCategoria(productoActualizado.getCategoria());

        if (productoActualizado.getActivo() != null) {
            producto.setActivo(productoActualizado.getActivo());
        }

        Producto guardado = productoRepository.save(producto);
        auditSqsService.enviarEventoAuditoria("MODIFICAR", guardado);
        return guardado;
    }

    public Producto eliminar(Long id) {
        Optional<Producto> productoOptional = productoRepository.findById(id);

        if (productoOptional.isEmpty()) {
            return null;
        }

        Producto producto = productoOptional.get();
        producto.setActivo(false);

        Producto guardado = productoRepository.save(producto);
        auditSqsService.enviarEventoAuditoria("ELIMINAR", guardado);
        return guardado;
    }

    private void validarProducto(Producto producto) {
        if (producto == null) {
            throw new RuntimeException("El producto es obligatorio");
        }

        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }

        if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }

        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        if (producto.getCategoria() == null || producto.getCategoria().isBlank()) {
            throw new RuntimeException("La categoría es obligatoria");
        }
    }
}
