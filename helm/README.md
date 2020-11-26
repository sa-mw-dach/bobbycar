# Bobbycar Helm Charts

To set up the minimum Bobbycar demo, you have to install the mandatory core components provided by the following Helm charts.

- bobbycar-core-infra
- bobbycar-core-apps

*bobbycar-core-infra* sets up the required operators and infrastructure components like Kafka, MQTT broker etc.

*bobbycar-core-apps* contains all required application components / microservices like the vehicle simulator, several Camel-K integrations etc.

All other Helm charts are optional.

## Set up Bobbycar from the Git repo

1. Clone the Bobbycar repo and navigate to helm subfolder

```sh
git clone https://github.com/sa-mw-dach/bobbycar.git

cd bobbycar/helm/
```

2. Create a namespace

```sh
oc new-project bobbycar
```

3. Edit the operator-group.yaml to match the correct namespace and apply.

```sh
oc apply -f operator-group.yaml
```

4. Install *bobbycar-core-infra* chart

Edit the bobbycar-core-infra Helm chart values.yaml, optionally validate the templates and install the chart

``` sh
vi bobbycar-core-infra/values.yaml

helm template bobbycar-core-infra/

helm install bobbycar-core-infra/ -g
```

Wait for around 2 minutes and verify that all components have been successfully installed.

5. Install *bobbycar-core-apps* chart

Edit the bobbycar-core-apps Helm chart values.yaml, optionally validate the templates and install the chart.

```sh
vi bobbycar-core-apps/values.yaml

helm template bobbycar-core-apps/

helm install bobbycar-core-apps/ -g
```
Verify that all components have been successfully installed.

Open the route URL of the dashboard component and get started.

## Set up Bobbycar from the Helm Chart repo