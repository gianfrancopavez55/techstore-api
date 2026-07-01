# techstore-audit-logger

Función AWS Lambda para la Evaluación Parcial 3.

- Runtime sugerido: Node.js 20.x o Node.js 22.x
- Nombre exacto: `techstore-audit-logger`
- Rol obligatorio en AWS Academy: `LabRole`
- Trigger: Amazon SQS `techstore-audit-queue`

La función consume los eventos JSON enviados por `techstore-api` y los registra en CloudWatch Logs.
