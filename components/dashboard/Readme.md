# Bobbycar dashboard component

The dashboard visualizes the simulated vehicles and the communication between the components.

## Running the application in dev mode

You can run the application in dev mode with the Ionic CLI:
```
ionic serve
```

## Building for prod and packaging the application

 `ionic build --prod`.

## Building the container image

`docker build -t quay.io/bobbycar/dashboard:latest --platform linux/amd64 .`

and 

`docker push quay.io/bobbycar/dashboard:latest`
