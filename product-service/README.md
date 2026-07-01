# product-service / techstore-api

Microservicio principal de TechStore para la EP3 AWS.

## Funcionalidades

- Login JWT.
- CRUD de productos.
- Eliminación lógica.
- Health check `/health`.
- Auditoría asíncrona hacia Amazon SQS en operaciones de escritura.

## Ejecutar localmente

Levantar PostgreSQL desde la raíz del proyecto:

```bash
docker compose -f docker-compose-postgres-local.yml up -d
```

Ejecutar sin auditoría SQS real:

```bash
set TECHSTORE_AUDIT_ENABLED=false
mvn spring-boot:run
```

## Docker

```bash
docker build -t techstore-api .
```

El Dockerfile usa multi-stage build, JRE Alpine y usuario no-root.

## Variables AWS ECS

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
SPRING_JPA_DDL_AUTO
SPRING_SQL_INIT_MODE
AWS_REGION
TECHSTORE_AUDIT_ENABLED
TECHSTORE_AUDIT_QUEUE_URL
TECHSTORE_JWT_SECRET
```
