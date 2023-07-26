

# Bobbycar car-simulator component

This vehicle simulation uses Quarkus, the Supersonic Subatomic Java Framework.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev -Dcom.redhat.bobbycar.carsim.route=test-classes/gps/gpx/test/
```

## Packaging the application

The application can be packaged using `./mvnw package`.

## Building the container image

`docker build -f src/main/docker/Dockerfile.jvm -t quay.io/bobbycar/car-simulator:latest --platform linux/amd64 .`

and 

`docker push quay.io/bobbycar/car-simulator:latest`


## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`