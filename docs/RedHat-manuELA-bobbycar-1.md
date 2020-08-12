![Red Hat logo supposed to be here][logo]

# Bobbycar - A hitchikers guide - quick overview

Bobbycar is a Microservices based cloud-native application and demo, highlighting OpenShift and most of the RH Middleware portfolio in a business relevant IoT context. It is also a sample implementation of an IoT Reference Architecture built with Red Hat products.

The demo aims to accelerate customers building IoT solutions faster with Red Hat technologies.
Bobbycars are actually vehicle simulators implemented in Quarkus, simulating cars and sending telemetry data to the IoT Cloud Gateway.
The data is being used in different data processing flows for visualization, realtime analytics, machine learning and other use cases.

![Map view][map_view]
<div style="text-align: left"><sup><span style="color:blue">(c) Google maps</span></sup></div>

## Background info

We try to cover several scenarios in a simplified way at the same time.

In order to achieve the above view on your fleet you need:
+ Connection to external systems like data providers
+ Connection to systems transferring their data via wire or even completely disconnected via air
+ There is a mixture of data
  + real time data
  + historic data
  + non real time data with low priority
  + different data protection needs for several data streams
  + data with high bandwidth needs (like onboard camera)
  + data with low bandwidth need (like geo location)

Depending on the data you refer to also different software components need to be used in order to address the several data stream constraints and applying security needs.

## Architectural components

The following architectural overview is meant to be a first overview to help understand the components we integrate

![Architectural overview][arch_overview]

The boxes in a slight red color show components where Red Hat products are used.

# Sources

**SA DACH Manufacturing repository** The Red Hat Manufacturing team maintains a GITHUB repository, where all the information is available. https://github.com/sa-mw-dach/bobbycar

**Quarkus** https://quarkus.io/ and https://github.com/quarkusio/quarkus

**AMQ** https://www.youtube.com/watch?v=CbBMocHD3p4

**Red Hat Datagrid** https://www.redhat.com/cms/managed-files/mi-jdg-tech-overview-us95017at-201607-en.pdf

**Red Hat OpenJDK** https://www.redhat.com/cms/managed-files/mi-openjdk-datasheet-f17057ck-201905-us.pdf

**Red Hat - Single Sign On - SSO** https://developers.redhat.com/blog/2018/03/19/sso-made-easy-keycloak-rhsso/

**Red Hat 3Scale** API management https://www.redhat.com/en/technologies/jboss-middleware/3scale

**Red Hat Fuse** https://www.redhat.com/en/technologies/jboss-middleware/fuse

**Red Hat Code ready Studio** https://www.redhat.com/en/technologies/jboss-middleware/developer-studio

**Code Ready Workspaces** https://developers.redhat.com/products/codeready-workspaces/overview/

**Visual Studio Extensions** https://developers.redhat.com/products/vscode_extensions/overview

**Camel K** https://camel.apache.org/camel-k/latest/index.html

**Opendatahub** https://opendatahub.io/

[logo]: images/rh_manuela_logo.png "I should be on the top"
[arch_overview]: images/bobbycar_architecture.png "I should show an architectural overview"
[map_view]: images/map_small.png "I should show a map here"
