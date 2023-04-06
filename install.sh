#!/bin/bash

set -e

log() {
  echo
  echo "##### $*"
}

source install_cleanup_vars.sh
test -f .env && source .env

# If weather api keys are empty, set a default value.
if [[ -z "$OWM_WEATHER_API_KEY" ]]; then
  OWM_WEATHER_API_KEY='my-owm-api-key';
fi;
if [[ -z "$IBM_WEATHER_API_KEY" ]]; then
  IBM_WEATHER_API_KEY='my-ibm-api-key';
fi;

log "Creating namespace $NAMESPACE for Bobbycar demo"
oc new-project "$NAMESPACE" || true


log "Installing the infra Helm release: $HELM_INFRA_RELEASE_NAME"
INFRA_OPTS=("-f" "helm/bobbycar-core-infra/values.yaml")
INFRA_OPTS+=("-f" "helm/bobbycar-core-infra/values.drogue.yaml")
INFRA_OPTS+=("--set-string" "global.domain=-${NAMESPACE}.${APP_DOMAIN}")

helm repo add drogue-iot https://drogue-iot.github.io/drogue-cloud-helm-charts/
helm dependency build helm/bobbycar-core-infra

helm upgrade --install "$HELM_INFRA_RELEASE_NAME" helm/bobbycar-core-infra/ \
"${INFRA_OPTS[@]}" \
--set-string namespace="$NAMESPACE" \
--set-string ocpDomain="$APP_DOMAIN" \
--set drogueIoT="$DROGUE_IOT"

sleep 30

log "Waiting for Datagrid pod"
oc wait --for=condition=Ready pod/bobbycar-dg-0 --timeout 300s
log "Waiting for Kafka Broker pod"
oc wait --for=condition=Ready pod/bobbycar-cluster-kafka-0 --timeout 300s

log "Installing the apps Helm release: $HELM_APP_RELEASE_NAME"
APPS_OPTS=("-f" "helm/bobbycar-core-apps/values.yaml")
APPS_OPTS+=("-f" "helm/bobbycar-core-apps/values.drogue.yaml")

helm upgrade --install "$HELM_APP_RELEASE_NAME" helm/bobbycar-core-apps \
"${APPS_OPTS[@]}" \
--set-string ocpDomain="$APP_DOMAIN" \
--set-string ocpApi="$API_DOMAIN" \
--set-string namespace="$NAMESPACE" \
--set-string dashboard.config.googleApiKey="$GOOGLE_API_KEY" \
--set-string weatherService.owm.api.key="$OWM_WEATHER_API_KEY" \
--set-string weatherService.ibm.api.key="$IBM_WEATHER_API_KEY" \
--set-string dashboard.config.ocpApiUrl="https://$API_DOMAIN:6443" \
--set drogueIoT="$DROGUE_IOT"

sleep 30

log "Waiting for Bobbycar pod"
oc wait --for=condition=Available deployment/car-simulator --timeout 300s
log "Waiting for Bobbycar Dashboard pod"
oc wait --for=condition=Available dc/dashboard --timeout 300s
log "Waiting for Dashboard Streaming service pod"
oc wait --for=condition=Available deployment/dashboard-streaming --timeout 300s

log "Installing the Serverless Helm release: $HELM_SERVERLESS_RELEASE_NAME"
helm upgrade --install "$HELM_SERVERLESS_RELEASE_NAME" helm/bobbycar-opt-serverless \
--set-string namespace="$NAMESPACE" \
--set-string otaServer.url="http://ota-server-bobbycar.$APP_DOMAIN"

log "Waiting for Camel-K integrations to complete..."
oc wait --for=condition=Ready integration/cache-service --timeout 1800s
oc wait --for=condition=Ready integration/kafka2datagrid --timeout 1800s
[[ "$DROGUE_IOT" != true ]] && oc wait --for=condition=Ready integration/mqtt2kafka --timeout 1800s

log "Installation completed!"
