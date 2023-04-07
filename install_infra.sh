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

sleep 60

log "Waiting for Datagrid pod"
oc wait --for=condition=Ready pod/bobbycar-dg-0 --timeout 300s

log "Waiting for Kafka Broker pod"
oc wait --for=condition=Ready pod/bobbycar-cluster-kafka-0 --timeout 300s

log "Infra Helm installation completed."
