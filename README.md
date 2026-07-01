# TechStore Cloud Platform - AWS Academy EP3

Proyecto académico desarrollado para la Evaluación Parcial N°3 de la asignatura JVY0101 Java: Diseño y Construcción de Soluciones Nativas en Nube.

La solución implementa una API REST para la gestión de productos de TechStore Chile, incorporando autenticación JWT, persistencia en base de datos, despliegue en contenedores, auditoría asíncrona y automatización CI/CD sobre servicios de AWS Academy Learner Lab.

## Integrantes

- Gianfranco Pavez Zamora
- Lamartine Petit Papa

## Objetivo del proyecto

Modernizar el servicio de productos de TechStore mediante una arquitectura cloud nativa, utilizando contenedores, servicios administrados, mensajería asíncrona, funciones serverless y monitoreo centralizado.

La solución contempla:

- Microservicio REST desarrollado con Java 17 y Spring Boot.
- Seguridad mediante autenticación JWT.
- Imagen Docker optimizada para ejecución en contenedores.
- Registro de imagen en Amazon ECR.
- Ejecución del contenedor en Amazon ECS Fargate.
- Exposición mediante Application Load Balancer.
- Publicación externa mediante Amazon API Gateway.
- Persistencia en Amazon RDS PostgreSQL.
- Auditoría asíncrona mediante Amazon SQS.
- Procesamiento serverless con AWS Lambda.
- Registro de eventos en Amazon CloudWatch Logs.
- Automatización de despliegue mediante GitHub Actions.

## Arquitectura de la solución

```text
Cliente / Postman
        |
        v
Amazon API Gateway
        |
        v
Application Load Balancer
        |
        v
Amazon ECS Fargate
techstore-api Spring Boot :8080
        |
        v
Amazon RDS PostgreSQL
        |
        v
Amazon SQS
techstore-audit-queue
        |
        v
AWS Lambda
techstore-audit-logger
        |
        v
Amazon CloudWatch Logs
```

## Componentes principales

### Product Service

Microservicio principal de la plataforma. Está desarrollado con Spring Boot y expone endpoints REST para autenticación, verificación de estado y administración de productos.

Ubicación:

```text
product-service/
```

Funciones principales:

- Autenticación de usuarios mediante JWT.
- Listado de productos.
- Consulta de producto por ID.
- Creación de productos.
- Modificación de productos.
- Eliminación lógica de productos.
- Envío de eventos de auditoría hacia Amazon SQS en operaciones de escritura.

### Amazon RDS PostgreSQL

Base de datos relacional utilizada para almacenar la información de productos. En ambiente cloud, el microservicio se conecta a una instancia PostgreSQL administrada por Amazon RDS.

Base de datos utilizada:

```text
techstore
```

### Amazon SQS

Servicio de cola utilizado para desacoplar el registro de auditoría del flujo principal de la API.

Cola utilizada:

```text
techstore-audit-queue
```

Las operaciones de escritura generan mensajes con información del evento realizado, incluyendo acción, producto, usuario y fecha.

Ejemplo de evento:

```json
{
  "accion": "CREAR",
  "productoId": 1,
  "nombre": "Producto TechStore",
  "usuario": "admin@techstore.cl",
  "fecha": "2026-07-01T00:00:00Z"
}
```

### AWS Lambda

Función serverless encargada de consumir los mensajes enviados a SQS y registrar la información de auditoría en CloudWatch Logs.

Función utilizada:

```text
techstore-audit-logger
```

Ubicación del código:

```text
serverless/aws-lambda/techstore-audit-logger/
```

### Amazon CloudWatch

Servicio utilizado para visualizar los logs generados por la función Lambda. Permite verificar que los eventos de auditoría fueron procesados correctamente después de crear, modificar o eliminar productos.

### Amazon ECS Fargate

Servicio utilizado para ejecutar el contenedor del microservicio sin administrar servidores directamente.

Servicio ECS:

```text
techstore-api-service
```

Cluster ECS:

```text
techstore-cluster
```

Task Definition:

```text
techstore-api-task
```

### Amazon ECR

Repositorio privado utilizado para almacenar la imagen Docker del microservicio.

Repositorio:

```text
techstore-api
```

Imagen:

```text
techstore-api:latest
```

### Application Load Balancer

Balanceador utilizado para exponer el servicio ECS Fargate mediante HTTP y distribuir tráfico hacia las tareas activas del contenedor.

### Amazon API Gateway

Servicio utilizado como punto de entrada externo hacia la API. API Gateway redirige las solicitudes hacia el Application Load Balancer, permitiendo consumir la aplicación desde una URL pública administrada.

### GitHub Actions

Pipeline CI/CD utilizado para automatizar el proceso de integración y despliegue.

Workflow:

```text
.github/workflows/deploy.yml
```

El pipeline realiza:

1. Obtención del código fuente.
2. Configuración de Java 17.
3. Ejecución de pruebas Maven.
4. Empaquetado de la aplicación.
5. Construcción de la imagen Docker.
6. Autenticación con Amazon ECR.
7. Publicación de la imagen Docker en ECR.
8. Actualización del servicio ECS Fargate.

## Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Security
- JWT
- Maven
- Docker
- PostgreSQL
- Amazon ECR
- Amazon ECS Fargate
- Amazon RDS PostgreSQL
- Amazon SQS
- AWS Lambda
- Amazon CloudWatch Logs
- Amazon API Gateway
- Application Load Balancer
- GitHub Actions
- Postman
- AWS Academy Learner Lab

## Estructura del repositorio

```text
techstore-cloud-platform-aws-final/
├── .github/
│   └── workflows/
│       └── deploy.yml
├── api-gateway/
├── aws/
├── infra/
├── order-service/
├── product-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── serverless/
│   └── aws-lambda/
│       └── techstore-audit-logger/
├── docker-compose.yml
├── docker-compose-cloud.yml
├── docker-compose-postgres-local.yml
├── docker-stack.yml
├── README.md
└── TechStore_Cloud_EP3.postman_collection.json
```

## Endpoints principales

### Health check

```http
GET /health
```

Permite validar que el microservicio se encuentra activo.

### Autenticación

```http
POST /auth/login
```

Body de ejemplo:

```json
{
  "username": "admin",
  "password": "1234"
}
```

Respuesta esperada:

```json
{
  "token": "jwt_token_generado"
}
```

### Productos

Los endpoints de productos requieren autenticación mediante JWT.

```http
GET /api/productos
GET /api/productos/{id}
POST /api/productos
PUT /api/productos/{id}
DELETE /api/productos/{id}
```

Header requerido:

```http
Authorization: Bearer <JWT_TOKEN>
```

Ejemplo de creación de producto:

```json
{
  "nombre": "Producto TechStore",
  "descripcion": "Producto de prueba para la plataforma",
  "precio": 69990,
  "stock": 20,
  "categoria": "Tecnologia",
  "activo": true
}
```

## Ejecución local

### Levantar PostgreSQL local

Desde la raíz del proyecto:

```bash
docker compose -f docker-compose-postgres-local.yml up -d
```

Verificar contenedores activos:

```bash
docker ps
```

### Ejecutar el microservicio localmente

Desde la carpeta del servicio:

```bash
cd product-service
mvn spring-boot:run
```

Para pruebas locales sin envío real a SQS, se puede desactivar la auditoría mediante variable de entorno:

```bash
TECHSTORE_AUDIT_ENABLED=false
```

En Windows CMD:

```bat
set TECHSTORE_AUDIT_ENABLED=false
mvn spring-boot:run
```

## Ejecución con Docker

Desde la raíz del proyecto:

```bash
docker compose up --build
```

También se puede construir la imagen del microservicio directamente:

```bash
cd product-service
docker build -t techstore-api:latest .
```

## Variables de entorno principales

Para el despliegue en ECS Fargate se utilizan variables de entorno asociadas a base de datos, región AWS, auditoría y cola SQS.

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://<RDS_ENDPOINT>:5432/techstore
SPRING_DATASOURCE_USERNAME=<RDS_USERNAME>
SPRING_DATASOURCE_PASSWORD=<RDS_PASSWORD>
SPRING_SQL_INIT_MODE=never
SPRING_JPA_SHOW_SQL=false
AWS_REGION=us-east-1
TECHSTORE_AUDIT_ENABLED=true
TECHSTORE_AUDIT_QUEUE_URL=<SQS_QUEUE_URL>
```

Las credenciales sensibles no deben almacenarse directamente en el repositorio.

## Despliegue en AWS

El despliegue cloud utiliza los siguientes recursos:

- Repositorio Amazon ECR para la imagen Docker.
- Cluster Amazon ECS con servicio Fargate.
- Task Definition para ejecutar el contenedor Spring Boot.
- Application Load Balancer para exponer el servicio.
- Amazon API Gateway como entrada pública.
- Base de datos Amazon RDS PostgreSQL.
- Cola Amazon SQS para eventos de auditoría.
- Función AWS Lambda conectada a la cola SQS.
- Amazon CloudWatch Logs para visualización de auditoría.

El rol utilizado en AWS Academy para los servicios desplegados corresponde a:

```text
LabRole
```

## CI/CD con GitHub Actions

El repositorio incluye un workflow de despliegue automático en:

```text
.github/workflows/deploy.yml
```

El workflow se ejecuta al hacer push a la rama principal o manualmente desde la pestaña Actions de GitHub.

Para ejecutar el pipeline en AWS Academy, se deben configurar los siguientes secretos en GitHub Actions:

```text
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_SESSION_TOKEN
```

Las credenciales de AWS Academy son temporales, por lo que deben actualizarse si el Learner Lab se reinicia o entrega nuevas credenciales.

## Colecciones Postman

El repositorio incluye colecciones Postman para probar los endpoints de autenticación, productos y despliegue cloud.

Archivos incluidos:

```text
TechStore API.postman_collection.json
TechStore_AWS_EP3.postman_collection.json
TechStore_Cloud_EP3.postman_collection.json
```

## Validación funcional

La validación de la solución considera:

- Inicio correcto del microservicio.
- Respuesta del endpoint `/health`.
- Login exitoso y generación de token JWT.
- Consumo de endpoints protegidos usando `Authorization: Bearer`.
- Creación de productos en la API.
- Persistencia de productos en PostgreSQL.
- Envío de eventos de auditoría hacia Amazon SQS.
- Ejecución automática de AWS Lambda desde mensajes SQS.
- Visualización de auditoría en Amazon CloudWatch Logs.
- Publicación de imagen Docker en Amazon ECR.
- Servicio ECS Fargate activo y saludable detrás del ALB.
- Consumo exitoso de la API mediante Amazon API Gateway.
- Ejecución exitosa del pipeline GitHub Actions.

## Seguridad

El proyecto evita almacenar credenciales reales dentro del repositorio.

Las credenciales de AWS Academy, contraseñas de base de datos, tokens JWT y claves temporales deben manejarse únicamente mediante variables de entorno, consola local o GitHub Secrets.

## Estado del proyecto

Proyecto funcional con API REST protegida mediante JWT, persistencia en PostgreSQL, despliegue en AWS ECS Fargate, entrada mediante API Gateway, auditoría asíncrona con SQS, procesamiento serverless con Lambda, logs en CloudWatch y automatización CI/CD mediante GitHub Actions.
