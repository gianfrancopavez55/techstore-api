#!/bin/sh

echo "Escalando lambda-consumer a 2 replicas..."
docker service scale techstore_lambda-consumer=2

echo "Escalando api-gateway a 2 replicas..."
docker service scale techstore_api-gateway=2

echo "Estado de servicios:"
docker service ls
