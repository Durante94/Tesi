#!/usr/bin/env bash

docker run -d --net=host --name=zookeeper-test -e ZOOKEEPER_ADMIN_ENABLE_SERVER=false -e ZOOKEEPER_CLIENT_PORT=2181 -e ZOOKEEPER_TICK_TIME=2000 -e ZOOKEEPER_SYNC_LIMIT=2 confluentinc/cp-zookeeper:latest
docker run -d --net=host --name=kafka-test -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_BROKER_ID=2 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 confluentinc/cp-kafka:latest

docker exec kafka-test kafka-topics --create --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 --topic alarm
docker exec kafka-test kafka-topics --create --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 --topic heartbeat

pytest --log-level=DEBUG .

docker stop kafka-test && docker rm kafka-test
docker stop zookeeper-test && docker rm zookeeper-test