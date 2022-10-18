# Cheet Sheet

## Build new images

```shell
pushd components/car-simulator
mvn package
podman build . -f src/main/docker/Dockerfile.jvm -t quay.io/ctrontesting/car-simulator:latest
podman push quay.io/ctrontesting/car-simulator:latest
popd
```

```shell
pushd components/dashboard-streaming
mvn package
podman build . -f src/main/docker/Dockerfile.jvm -t quay.io/ctrontesting/dashboard-streaming:latest
podman push quay.io/ctrontesting/dashboard-streaming:latest
popd
```

```shell
pushd components/dashboard
npm run build
podman build . -t quay.io/ctrontesting/dashboard:latest
podman push quay.io/ctrontesting/dashboard:latest
popd
```

## Log a Kafka topic

```shell
podman run --rm -ti docker.io/bitnami/kafka:latest kafka-console-consumer.sh \
  --topic {topic} \
  --bootstrap-server {bootstrap}
```
