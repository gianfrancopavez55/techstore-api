#!/bin/sh

echo "Construyendo imagen lambda-consumer..."
docker build -t techstore-lambda-consumer:latest ../../serverless/lambda-consumer

echo "Desplegando stack TechStore Cloud..."
docker stack deploy -c ../../docker-stack.yml techstore

echo "Servicios desplegados:"
docker service ls
