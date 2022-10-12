# Bobbycar

Bobbycar is a distributed cloud-native application that implements **key aspects** of a modern **IoT architecture** in an exemplary manner. 
This demo is based on Red Hat's Kubernetes Distribution, **Red Hat OpenShift Container Platform**, and uses various middleware components optimized for a cloud-native usage.

## About

Bobbycars are actually vehicle simulators implemented in **Quarkus**, simulating cars and sending telemetry data to an IoT Cloud Gateway.

The data is then being used in different data processing flows for visualization, realtime analytics, machine learning and other use cases.

## Installation

You can use the **install.sh** script to simply install demo.

1. Fill in the mandatory properties in **install_cleanup_vars.sh**, i.e.:

   ```
   NAMESPACE=bobbycar
   APP_DOMAIN=apps.ocp.domain
   API_DOMAIN=api.ocp.domain
   GOOGLE_API_KEY=here_goes_my_maps_api_key
   ```

2. Run the **install.sh** script

3. Run the **cleanup.sh** script to uninstall the demo

If you want to manually install Bobbycar in your own environment, please follow these [installation instructions!](https://github.com/sa-mw-dach/bobbycar/tree/master/helm)

## Purpose

+ Accelerate building IoT solutions faster with Red Hat technologies.
  
+ Bobbycar aims to showcase the COMPLETE Red Hat Middleware stack on OpenShift in a real world IoT context.

+ Getting a more realistic hands-on experience with cloud-native development and OpenShift

## Technologies

+ RH OpenShift, Enterprise Kubernetes
+ RH OpenShift Serverless, based on Knative  
+ RH Runtimes, Quarkus
+ RH AMQ Broker, MQTT
+ RH AMQ Streams, Apache Kafka
+ RH Fuse, Apache Camel-K
+ RH Datagrid, Distributed Caching
+ RH OpenShift Pipelines
+ RH CodeReady Workspaces
+ RH OpenJDK
+ Angular, Ionic
+ Gogs, Nexus, Sonarqube

