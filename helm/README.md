# Bobbycar Manual Installation
To set up the core Bobbycar demo, you have to install the required operators and the mandatory core components provided by two Helm charts.

### 1. Create a new OpenShift Project
````sh
oc new-project bobbycar
````

### 2. Install the required Operators

Log in to the OpenShift Admin Console with a privileged user account and install the following operators in the bobbycar namespace:  

**1. AMQ Streams**  
startingCSV: amqstreams.v2.0.0-0
channel: stable
installPlanApproval: Automatic

**2. AMQ Broker**
startingCSV: amq-broker-operator.v7.9.1-opr-2
channel: 7.x
installPlanApproval: Automatic

**3. Datagrid**  
startingCSV: datagrid-operator.v8.2.8
channel: 8.2.x
installPlanApproval: Automatic

**4. Camel-K**
startingCSV: red-hat-camel-k-operator.v1.6.3
channel: 1.6.x
installPlanApproval: Automatic

### 3. Install the Bobbycar Helm Charts

There are 2 Helm charts, that needs to be installed:

**bobbycar-core-infra** sets up and configures the required infrastructure components (Kafka, Kafka Topics, MQTT broker, Camel-K, Datagrid).

**bobbycar-core-apps** sets up all required application components like the vehicle simulator, several Camel-K integrations etc.

Please install them in the following order:
1. bobbycar-core-infra
2. bobbycar-core-apps

### 4.1 Helm Installation Options

You have basically two options to install Bobbycar.
1. Configure the Bobbycar Helm Chart Repository in OpenShift (min. 4.6.x) and install Bobbycar UI based from the OpenShift Development Console.
2. Use the Helm CLI to install Bobbycar from the commandline.

#### 4.1.1 Option 1:

#### 1. Configure Helm chart repository for OpenShift

```sh
oc apply -f https://raw.githubusercontent.com/sa-mw-dach/bobbycar/master/helm/helm-repo.yaml
```

#### 2. Install the Helm charts from the OpenShift Developer Catalog

1. Open the Browser and login to your OCP environment.
2. Switch to the Developer perspective and the go to the namespace bobbycar
3. Click the +ADD button and choose Helm Charts
4. Now you should see the Bobbycar Helm charts from the Helm repo. Click on the Helm chart and install them in the right order.

#### 4.1.2 Option 2:

#### 2. Install with Helm CLI

Add the bobbycar Helm repo and list the charts.
```sh
helm repo add bobbycar-repo https://sa-mw-dach.github.io/bobbycar-charts/
helm repo update
helm search repo bobbycar-repo

NAME                                 	CHART VERSION	APP VERSION	DESCRIPTION
bobbycar-repo/bobbycar-core-apps     	1.0.2        	4.6.12     	Bobbycar core infrastructure components
bobbycar-repo/bobbycar-core-infra    	1.1.0        	4.6.12     	Bobbycar core infrastructure components
bobbycar-repo/bobbycar-core-operators	1.0.0        	4.6.12     	Bobbycar core operators
```

You can check the values of every chart (and create your values file). 
```sh
helm show values bobbycar-repo/bobbycar-core-infra

helm show values bobbycar-repo/bobbycar-core-infra > my-core-infra-values.yaml
```

**Install the Helm charts:**

1. bobbycar-core-infra:  

You **need** to adjust these values:
- namespace
- ocpDomain

```sh
helm install bobbycar-core-infra bobbycar-repo/bobbycar-core-infra --set-string namespace=bobbycar --set-string ocpDomain=apps.ocp3.stormshift.coe.muc.redhat.com

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

2. bobbycar-core-apps:

3. You **need** to adjust these values:
- namespace
- ocpDomain
- ocpApi
- dashboard.config.googleApiKey

```sh
helm install bobbycar-core-apps bobbycar-repo/bobbycar-core-apps \
--set-string ocpDomain=apps.ocp3.stormshift.coe.muc.redhat.com \
--set-string ocpApi=api.ocp3.stormshift.coe.muc.redhat.com \
--set-string namespace=bobbycar \
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

Open the Bobbycar Dashboard URL and get started.
```sh
oc get route dashboard

NAME        HOST/PORT                                                    PATH   SERVICES    PORT       TERMINATION   WILDCARD
dashboard   dashboard-bobbycar.apps.ocp3.stormshift.coe.muc.redhat.com          dashboard   8080-tcp                 None
```