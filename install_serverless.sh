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

log "Installing the Serverless Helm release: $HELM_SERVERLESS_RELEASE_NAME"
helm upgrade --install "$HELM_SERVERLESS_RELEASE_NAME" helm/bobbycar-opt-serverless \
--set-string namespace="$NAMESPACE" \
--set-string otaServer.url="http://ota-server-bobbycar.$APP_DOMAIN"

log "Waiting for Camel-K integrations to complete..."
oc wait --for=condition=Ready integration/cache-service --timeout 1800s
oc wait --for=condition=Ready integration/kafka2datagrid --timeout 1800s

log "Serverless Helm installation completed."
