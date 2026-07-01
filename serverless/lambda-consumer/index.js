const amqp = require("amqplib");

const QUEUE_NAME = "order.created";
const RABBITMQ_URL = process.env.RABBITMQ_URL || "amqp://guest:guest@rabbitmq:5672";

async function startConsumer() {
  try {
    const connection = await amqp.connect(RABBITMQ_URL);
    const channel = await connection.createChannel();

    await channel.assertQueue(QUEUE_NAME, { durable: true });

    console.log("======================================");
    console.log(" Lambda Consumer iniciado correctamente");
    console.log(` Esperando mensajes en la cola: ${QUEUE_NAME}`);
    console.log("======================================");

    channel.consume(QUEUE_NAME, (message) => {
      if (message !== null) {
        const content = message.content.toString();

        console.log("\nMensaje recibido desde RabbitMQ:");
        console.log(content);

        const order = JSON.parse(content);

        console.log("\nProcesando pedido...");
        console.log(`Cliente: ${order.cliente}`);
        console.log(`Producto ID: ${order.productoId}`);
        console.log(`Cantidad: ${order.cantidad}`);
        console.log(`Direccion: ${order.direccion}`);
        console.log(`Estado: PEDIDO_PROCESADO_ASINCRONAMENTE`);

        channel.ack(message);

        console.log("Mensaje confirmado y eliminado de la cola.\n");
      }
    });
  } catch (error) {
    console.error("Error conectando a RabbitMQ:", error.message);
    console.log("Reintentando conexion en 5 segundos...");
    setTimeout(startConsumer, 5000);
  }
}

startConsumer();
