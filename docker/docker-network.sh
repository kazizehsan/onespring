#!/bin/sh

docker network inspect onespring-network >/dev/null 2>&1 ||
  docker network create \
    --driver=bridge \
    onespring-network
