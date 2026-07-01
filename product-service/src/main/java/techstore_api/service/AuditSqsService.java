package techstore_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import techstore_api.model.Producto;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuditSqsService {

    private static final Logger log = LoggerFactory.getLogger(AuditSqsService.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${techstore.audit.enabled:false}")
    private boolean auditEnabled;

    @Value("${techstore.audit.queue-url:}")
    private String queueUrl;

    public AuditSqsService(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @Async
    public void enviarEventoAuditoria(String accion, Producto producto) {
        if (!auditEnabled) {
            log.info("Auditoría SQS desactivada. Acción={} ProductoId={}", accion, producto != null ? producto.getId() : null);
            return;
        }

        if (queueUrl == null || queueUrl.isBlank()) {
            log.warn("Auditoría SQS activada, pero TECHSTORE_AUDIT_QUEUE_URL no está configurada.");
            return;
        }

        if (producto == null) {
            log.warn("No se envió auditoría SQS porque el producto es null. Acción={}", accion);
            return;
        }

        try {
            Map<String, Object> evento = new LinkedHashMap<>();
            evento.put("accion", accion);
            evento.put("productoId", producto.getId());
            evento.put("nombre", producto.getNombre());
            evento.put("usuario", obtenerUsuarioActual());
            evento.put("fecha", Instant.now().toString());

            String mensajeJson = objectMapper.writeValueAsString(evento);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(mensajeJson)
                    .build();

            sqsClient.sendMessage(request);
            log.info("Evento de auditoría enviado a SQS: {}", mensajeJson);
        } catch (Exception e) {
            // No rompemos el CRUD por un fallo de auditoría; queda registrado para CloudWatch/ECS logs.
            log.error("Error enviando evento de auditoría a SQS. Acción={}", accion, e);
        }
    }

    private String obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return "admin@techstore.cl";
        }

        String usuario = authentication.getName();
        if ("admin".equalsIgnoreCase(usuario)) {
            return "admin@techstore.cl";
        }

        return usuario;
    }
}
