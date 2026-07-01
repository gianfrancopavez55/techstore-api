#!/bin/sh

echo "Inicializando Docker Swarm..."
docker swarm init

echo "Docker Swarm inicializado correctamente."
docker node ls
