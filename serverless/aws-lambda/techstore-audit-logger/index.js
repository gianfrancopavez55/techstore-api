exports.handler = async (event) => {
  console.log('TechStore audit logger iniciado');
  console.log('Cantidad de mensajes recibidos:', event.Records?.length || 0);

  for (const record of event.Records || []) {
    try {
      const auditEvent = JSON.parse(record.body);

      console.log(JSON.stringify({
        tipo: 'TECHSTORE_AUDITORIA_INVENTARIO',
        accion: auditEvent.accion,
        productoId: auditEvent.productoId,
        nombre: auditEvent.nombre,
        usuario: auditEvent.usuario,
        fecha: auditEvent.fecha,
        messageId: record.messageId
      }));
    } catch (error) {
      console.error('Error procesando mensaje SQS:', {
        messageId: record.messageId,
        body: record.body,
        error: error.message
      });
      throw error;
    }
  }

  return {
    statusCode: 200,
    body: JSON.stringify({ processed: event.Records?.length || 0 })
  };
};
