# Bobbycar Helm Charts

To set up the core Bobbycar demo, you have to install the mandatory core components provided by the following Helm charts.

- bobbycar-core-operators (cluster-admin privileges needed)
- bobbycar-core-infra
- bobbycar-core-apps

**bobbycar-core-operators** sets up the required operators.

**bobbycar-core-infra** sets up and configures the required infrastructure components (Kafka, Kafka Topics, MQTT broker, Camel-K, Datagrid).

**bobbycar-core-apps** sets up all required application components like the vehicle simulator, several Camel-K integrations etc.

All other Helm charts are optional extensions.

## Bobbycar Installation Options

You have basically two options to install Bobbycar.
1. Configure the Bobbycar Helm Chart Repository in OpenShift (min. 4.6.x) and install Bobbycar UI based from the OpenShift Development Console.
2. Use the Helm CLI to install Bobbycar from the commandline.

**The order you install the Helm charts is important Please install in this order:**
1. bobbycar-core-operators
2. bobbycar-core-infra
3. bobbycar-core-apps

## Option 1:

Important: You need **cluster-admin privileges** in order to install the bobbycar-core-operators chart.

### 1. Configure Helm chart repository for OpenShift

```sh
oc apply -f https://raw.githubusercontent.com/sa-mw-dach/bobbycar/master/helm/helm-repo.yaml
```

### 2. Install the Helm charts from the OpenShift Developer Catalog

1. Open the Browser and login to your OCP environment.
2. Create a new OpenShift project/namespace where you want to install the demo.
3. Switch to the Developer perspective
4. Click the +ADD button and choose Helm Charts
5. Now you should see the Bobbycar Helm charts from the Helm repo. Click on the Helm chart and install them in the right order.

## Option 2: 

Important: You need **cluster-admin privileges** in order to install the bobbycar-core-operators chart.

### 1. Create the namespace where you want to install the Bobbycar demo

```sh
oc new-project bobbycar
```

### 2. Install the Helm charts

Add the bobbycar Helm repo and list the charts.
```sh
helm repo add bobbycar-repo https://sa-mw-dach.github.io/bobbycar-charts/

helm search repo bobbycar-repo

NAME                                 	CHART VERSION	APP VERSION	DESCRIPTION
bobbycar-repo/bobbycar-core-apps     	1.0.2        	4.6.12     	Bobbycar core infrastructure components
bobbycar-repo/bobbycar-core-infra    	1.1.0        	4.6.12     	Bobbycar core infrastructure components
bobbycar-repo/bobbycar-core-operators	1.0.0        	4.6.12     	Bobbycar core operators
```

You can check the values of every chart (and create your values file). 
```sh
helm show values bobbycar-repo/bobbycar-core-operators

helm show values bobbycar-repo/bobbycar-core-operators > my-core-operators-values.yaml
```

Install the Helm charts in the right order:
#### 1. bobbycar-core-operators
```sh
helm install bobbycar-core-operators bobbycar-repo/bobbycar-core-operators --set-string namespace=bobbycar

or
 
helm install -f my-core-operators-values.yaml bobbycar-core-operators bobbycar-repo/bobbycar-core-operators
```
Wait until the operators have been installed (PHASE: Succeeded)
```sh
watch oc get csv

NAME                                  DISPLAY                                         VERSION   REPLACES                             PHASE
amq-broker-operator.v0.18.0           Red Hat Integration - AMQ Broker                0.18.0    amq-broker-operator.v0.17.0          Succeeded
amqstreams.v1.6.2                     Red Hat Integration - AMQ Streams               1.6.2     amqstreams.v1.6.1                    Succeeded
datagrid-operator.v8.0.3              Data Grid                                       8.0.3     datagrid-operator.v8.0.2             Succeeded
red-hat-camel-k-operator.v1.2.1       Red Hat Integration - Camel K                   1.2.1     red-hat-camel-k-operator.v1.2.0      Succeeded

oc get pods

amq-broker-operator-cfcc59857-rqm7x                    1/1     Running     0          1m
amq-streams-cluster-operator-v1.6.2-6bdd5495cd-x7xrg   1/1     Running     0          1m
camel-k-operator-75b8975699-b6pjg                      1/1     Running     0          1m
infinispan-operator-97877968b-q6mb5                    1/1     Running     0          1m
```

#### 2. bobbycar-core-infra
```sh
helm install bobbycar-core-infra bobbycar-repo/bobbycar-core-infra --set-string namespace=bobbycar --set-string ocpDomain=api.ocp3.stormshift.coe.muc.redhat.com

or
 
helm install -f my-core-infra-values.yaml bobbycar-core-infra bobbycar-repo/bobbycar-core-infra
```
Wait until the components have been installed...
```sh
watch oc get pods

NAME                                                   READY   STATUS      RESTARTS   AGE
bobbycar-amq-mqtt-ss-0                                 1/1     Running     0          3d2h
bobbycar-bridge-75568d5f8f-hb5cq                       1/1     Running     0          26h
bobbycar-cluster-kafka-0                               1/1     Running     0          26h
bobbycar-cluster-kafka-1                               1/1     Running     0          26h
bobbycar-cluster-zookeeper-0                           1/1     Running     0          26h
bobbycar-cluster-zookeeper-1                           1/1     Running     0          26h
bobbycar-cluster-zookeeper-2                           1/1     Running     0          26h
bobbycar-dg-0                                          1/1     Running     0          3d2h

oc get KafkaTopic
oc get KafkaBridge
oc get Infinispan
```

### 3. Install *bobbycar-core-apps* chart

**Now get the datagrid operator password from the bobbycar-dg-generated-secret secret.**

```sh
helm install bobbycar-core-apps bobbycar-repo/bobbycar-core-apps \
--set-string ocpDomain=apps.ocp3.stormshift.coe.muc.redhat.com \
--set-string ocpApi=api.ocp3.stormshift.coe.muc.redhat.com \
--set-string namespace=bobbycar \
--set-string datagrid.account.password=YourPassword \
--set-string dashboard.config.googleApiKey=YourGoogleApiKey

or
 
helm install -f my-core-apps-values.yaml bobbycar-core-apps bobbycar-repo/bobbycar-core-apps
```
Wait until the app components have been build and installed...

```sh
watch oc get pods

NAME                                                   READY   STATUS      RESTARTS   AGE
cache-service-7c5dc67b6-2ltnj                          1/1     Running     0          3d2h
camel-k-kit-c07vq2m293usl0ms8geg-1-build               0/1     Completed   0          3d2h
camel-k-kit-c07vq2m293usl0ms8gf0-1-build               0/1     Completed   0          3d2h
camel-k-kit-c07vq2m293usl0ms8gfg-1-build               0/1     Completed   0          3d2h
camel-k-kit-c07vq2m293usl0ms8gg0-1-build               0/1     Completed   0          3d2h
car-simulator-1-8lwbv                                  1/1     Running     0          3d2h
car-simulator-1-build                                  0/1     Completed   0          3d2h
car-simulator-1-deploy                                 0/1     Completed   0          3d2h
car-simulator-native-1-build                           0/1     Completed   0          3d2h
dashboard-1-build                                      0/1     Completed   0          3d2h
dashboard-1-deploy                                     0/1     Completed   0          3d2h
dashboard-1-l9x2j                                      1/1     Running     0          3d2h
dashboard-streaming-service-7d8d6f769-5dhkk            1/1     Running     0          3d2h
kafka2datagrid-6df5456c58-p45cs                        1/1     Running     0          3d2h
mqtt2kafka-5494768b8b-4gc9b                            1/1     Running     0          3d2h
```

Open the dashboard URL and get started.
```sh
oc get route dashboard

NAME        HOST/PORT                                                    PATH   SERVICES    PORT       TERMINATION   WILDCARD
dashboard   dashboard-bobbycar.apps.ocp3.stormshift.coe.muc.redhat.com          dashboard   8080-tcp                 None
```