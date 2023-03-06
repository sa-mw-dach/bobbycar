# About Bobbycar - The Red Hat Connected Vehicle Architecture

Bobbycar is a **Red Hat solution pattern** that implements key aspects of a modern IoT/Edge architecture in an exemplary manner. 
It uses [Red Hat OpenShift Container Platform]( https://docs.openshift.com/container-platform/4.12/welcome/index.html) and various middleware components optimized for a cloud-native usage.

This enterprise architecture can serve as a foundation for an **IoT/Edge Hybrid Cloud environment** supporting various use cases like OTA deployments, Driver monitoring, AI/ML and others.

Bobbycar aims to showcase an end-to-end workflow, from connecting in-vehicle components to a cloud backend, processing telemetry data in batch or as stream, training AI/ML models and deploying containers through a DevSecOps pipeline and by leveraging GitOps to the edge.

# Purpose / Objectives

+ How to use Red Hat technologies to build a cloud-native Hybrid Cloud environment supporting automotive use-cases.

+ Accelerate building IoT solutions faster with Red Hat technologies.

+ Bobbycar aims to showcase the COMPLETE Red Hat Middleware stack on OpenShift in a real world IoT context.

+ Getting a more realistic hands-on experience with cloud-native development and OpenShift

# Getting Started - The Installation

You can use the `install.sh` script to install this Solution Pattern.

## Prerequisites

Before executing the `install.sh` script, make sure you have the following ready:

* A running OpenShift cluster environment (OCP on-premise, ROSA, OCP on IBM Cloud, ARO, OCP Dedicated) or [OpenShift Local](https://developers.redhat.com/products/openshift-local/overview)
* You have `cluster-admin` privileges for the OpenShift cluster 
* [OpenShift CLI (oc) ](https://docs.openshift.com/container-platform/4.12/cli_reference/openshift_cli/getting-started-cli.html#installing-openshift-cli)
* [Helm CLI](https://helm.sh/docs/intro/install/)
* A valid [Google Maps API Key](https://developers.google.com/maps/documentation/javascript/get-api-key)
* Gather the API and App domain of your OpenShift Cluster:
  * App domain example: `apps.cluster-zxf6m.zxf6m.sandbox1856.opentlc.com`
  * API domain example: `api.cluster-zxf6m.zxf6m.sandbox1856.opentlc.com` 
* [A clone of this Git repository](https://github.com/sa-mw-dach/bobbycar)
 
## Running the installation 

1. Provide the properties in `install_cleanup_vars.sh`, i.e.:

```shell
# The namespace to deploy the solution pattern in
NAMESPACE=bobbycar
# The clusters app domain without port
APP_DOMAIN=apps.cluster-pnc6l.pnc6l.sandbox52.opentlc.com
# The clusters api domain without port
API_DOMAIN=api.cluster-pnc6l.pnc6l.sandbox52.opentlc.com

# Google Maps API key - https://developers.google.com/maps/documentation/javascript/get-api-key
GOOGLE_API_KEY=<MyGoogleMapsApiKey>
# OpenWeatherMap API Key - https://openweathermap.org/api
OWM_WEATHER_API_KEY=<MyApiKey>
# https://www.ibm.com/products/environmental-intelligence-suite/data-packages
IBM_WEATHER_API_KEY=<MyApiKey>

# Installs OpenShift Serverless in the cluster
INSTALL_KNATIVE=true
# Installs the required namespaced operators: AMQ Streams, AMQ Broker, Datagrid, Camel-K
INSTALL_OPERATORS=true
# Deletes the BobbycarZone CRD when executing ./cleanup.sh
DELETE_CRD=true

# Helm Release names
HELM_INFRA_RELEASE_NAME=infra
HELM_APP_RELEASE_NAME=apps
HELM_SERVERLESS_RELEASE_NAME=serverless
 ```

Mandatory fields are:
* NAMESPACE
* APP_DOMAIN
* API_DOMAIN
* GOOGLE_API_KEY

The `weather-api` fields are optional:

* OWM_WEATHER_API_KEY and/or
* IBM_WEATHER_API_KEY

If you haven't installed OpenShift Serverless in your cluster, you can set `INSTALL_KNATIVE=true`, this will install OpenShift Serverless.

`INSTALL_OPERATORS=true` will install the 
* AMQ Streams
* AMQ Broker
* Camel-K
* Datagrid

operators in the local namespace you'll deploy this solution pattern into.

When using [OpenShift Local](https://developers.redhat.com/products/openshift-local/overview),
you can use the following values:
 
```shell
NAMESPACE=bobbycar
APP_DOMAIN=apps-crc.testing
API_DOMAIN=api-crc-testing
```

2. Running the `install.sh` script

  ```shell
  ./install.sh 
  ```

If you want to manually install Bobbycar in your own environment (step-by-step), please follow these [installation instructions!](https://github.com/sa-mw-dach/bobbycar/tree/master/helm)

## Clean up the installation

1. Run the `cleanup.sh` script to uninstall the solution pattern

If you also want to remove the `BobbycarZone CRD`, set `DELETE_CRD=true` in the `install_cleanup_vars.sh` script.

  ```shell
  ./cleanup.sh 
  ```

# Technologies used in this Solution Pattern

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

