# Bobbycar Helm Charts

To set up the minimum Bobbycar demo, you have to install the mandatory core components provided by the following Helm charts.

- bobbycar-core-infra
- bobbycar-core-apps

*bobbycar-core-infra* sets up the required operators and infrastructure components like Kafka, MQTT broker etc.

*bobbycar-core-apps* contains all required application components / microservices like the vehicle simulator, several Camel-K integrations etc.

All other Helm charts are optional.

## Set up Bobbycar from the Git repo

### 1. Clone the Bobbycar repo and navigate to helm subfolder

```sh
git clone https://github.com/sa-mw-dach/bobbycar.git

cd bobbycar/helm/
```

### 2. Create the namespace where you want to install the Bobbycar demo

```sh
oc new-project bobbycar
```

### 3. Install the *bobbycar-core-infra* Helm chart


Either edit the chart values.yaml or override the values with the commandline flags to install the chart.

**Adjusting these values to your environment is mandatory:**
- ocpDomain
- namespace

You can leave the others as default values if you like.

Option 1:
```sh
helm install bobbycar-core-infra bobbycar-core-infra/ --set-string ocpDomain=apps.ocp4.stormshift.coe.muc.redhat.com --set-string namespace=bobbycar
```
Option 2:
```sh
vi bobbycar-core-infra/values.yaml

helm install bobbycar-core-infra bobbycar-core-infra/
```

Wait for around 2 minutes and verify that all components have been successfully installed.

### 4. Install *bobbycar-core-apps* chart

Either edit the chart values.yaml or override the values with the commandline flags to install the chart.

**Adjusting these values to your environment is mandatory:**
- ocpDomain
- ocpApi  
- namespace
- datagrid.account.password
- dashboard.config.googleApiKey

**Get the datagrid operator password from the bobbycar-dg-generated-secret secret.**

You can leave the others as default values if you like.

Option 1:
```sh
helm install bobbycar-core-apps bobbycar-core-apps/ \
--set-string ocpDomain=apps.ocp4.stormshift.coe.muc.redhat.com \
--set-string ocpApi=api.ocp4.stormshift.coe.muc.redhat.com \
--set-string namespace=bobbycar \
--set-string datagrid.account.password=YourPassword \
--set-string dashboard.config.googleApiKey=YourGoogleApiKey
```

Option 2:
```sh
vi bobbycar-core-apps/values.yaml

helm install bobbycar-core-apps bobbycar-core-apps/
```
Verify that all components have been successfully installed.

Open the route URL of the dashboard component and get started.
