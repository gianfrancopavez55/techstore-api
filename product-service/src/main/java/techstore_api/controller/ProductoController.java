package techstore_api.controller;

import techstore_api.model.Producto;
import techstore_api.service.ProductoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {

        return ResponseEntity.ok(productoService.listarTodos());
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {

        Producto producto = productoService.buscarPorId(id);

        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Producto no encontrado");
        }

        return ResponseEntity.ok(producto);
    }

    // CREAR PRODUCTO
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Producto producto) {

        try {

            Producto nuevoProducto = productoService.crear(producto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(nuevoProducto);

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // ACTUALIZAR PRODUCTO
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Producto producto) {

        Producto productoActualizado =
                productoService.actualizar(id, producto);

        if (productoActualizado == null) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Producto no encontrado");
        }

        return ResponseEntity.ok(productoActualizado);
    }

    // ELIMINAR PRODUCTO (LÓGICO)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Producto productoEliminado = productoService.eliminar(id);

        if (productoEliminado == null) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Producto no encontrado");
        }

        return ResponseEntity.ok("Producto desactivado correctamente");
    }
}