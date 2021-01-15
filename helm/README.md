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

### 3. Edit the operator-group.yaml to match the correct namespace and apply the file.

```sh
oc apply -f operator-group.yaml
```

### 4. Install the *bobbycar-core-infra* Helm chart

Edit the chart values.yaml and install the chart

**Adjusting these fields to your environement is mandatory:**
- ocpDomain
- namespace

You can leave the others as default values if you like.


``` sh
vi bobbycar-core-infra/values.yaml

helm install bobbycar-core-infra/ -g
```

Wait for around 2 minutes and verify that all components have been successfully installed.

### 5. Install *bobbycar-core-apps* chart

Edit the chart values.yaml and install the chart.

**Adjusting these fields to your environement is mandatory:**
- ocpDomain
- ocpApi  
- namespace
- datagrid.account.password
- dashboard.config.googleApiKey

Get the datagrid password from the **bobbycar-dg-generated-secret** secret.

You can leave the others as default values if you like.
```sh
vi bobbycar-core-apps/values.yaml

helm install bobbycar-core-apps/ -g
```
Verify that all components have been successfully installed.

Open the route URL of the dashboard component and get started.

## Set up Bobbycar from the Helm Chart repo