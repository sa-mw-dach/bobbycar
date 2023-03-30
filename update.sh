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

oc project "$NAMESPACE"

log "Installing the apps Helm release: $HELM_APP_RELEASE_NAME"
APPS_OPTS=("-f" "helm/bobbycar-core-apps/values.yaml")
if [[ "$DROGUE_IOT" == true ]]; then
  APPS_OPTS+=("-f" "helm/bobbycar-core-apps/values.drogue.yaml")
fi
helm upgrade --dry-run "$HELM_APP_RELEASE_NAME" helm/bobbycar-core-apps \
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

log "Installation completed! Open the Bobbycar dashboard and get started:"
oc get route dashboard -o json | jq -r .spec.host
