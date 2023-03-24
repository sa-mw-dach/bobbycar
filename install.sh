#!/bin/bash

set -e

log() {
  echo
  echo "##### $*"
}

terminate() {
    log "$*"
    exit 1
}

wait_for_resource() {
  local timeout=$(($(date +%s) + 300))

  while ((timeout > $(date +%s))); do
    [[ "$(oc get -n "$NAMESPACE" "$@" -o 'go-template={{len .items}}' 2>/dev/null)" -gt 0 ]] && break
    sleep 5
  done

  if [[ ${timeout} < "$(date +%s)" ]]; then
      terminate "Error: timed out while waiting for '$*' (in namespace: $NAMESPACE) to exist."
  fi
}

# Wait for an operator to exist and be ready.
#
# $1 the label of the operator, for showing the user
# $2 the name of the operator resource (.spec.name from Subscription)
wait_for_operator() {
  local name="$1"
  shift
  local subscription="$1"
  shift

  local sel="deployment -l operators.coreos.com/${subscription}.${NAMESPACE}"

  log "Waiting for $name operator"
  # shellcheck disable=SC2086
  wait_for_resource $sel
  # shellcheck disable=SC2086
  oc -n "${NAMESPACE}" wait --for=condition=Available $sel --timeout=300s
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

if [[ "$INSTALL_KNATIVE" == true ]]; then
log "Installing OpenShift Serverless operator"
oc apply -f config/operators/serverless-operator-subscription.yaml
sleep 25
log "Waiting for OpenShift Serverless operator"
oc -n openshift-serverless wait --for=condition=Available deployment -l operators.coreos.com/serverless-operator.openshift-serverless --timeout=300s
log "Installing Knative Serving config"
oc apply -f config/knative/knative-serving.yaml
log "Installing Knative Eventing config"
oc apply -f config/knative/knative-eventing.yaml
log "Installing Knative Kafka config"
oc apply -f config/knative/knative-kafka.yaml
sleep 10
oc wait --for=condition=Ready knativeservings.v1beta1.operator.knative.dev/knative-serving --timeout 500s -n knative-serving
oc wait --for=condition=Ready knativeeventings.v1beta1.operator.knative.dev/knative-eventing --timeout 500s -n knative-eventing
oc wait --for=condition=Ready knativekafkas.v1alpha1.operator.serverless.openshift.io/knative-kafka --timeout 500s -n knative-eventing
log "OpenShift Serverless Installation has been completed!!"
fi ;

if [[ "$INSTALL_OPERATORS" == true ]]; then
log "Creating namespace $NAMESPACE for Bobbycar demo"
oc new-project "$NAMESPACE" || true
log "Installing operator group"
sed "s:{{NAMESPACE}}:$NAMESPACE:g" config/operators/operator-group.yaml | oc apply -f -
if [[ "$DROGUE_IOT" != true ]]; then
  log "Installing the AMQ Broker operator"
  sed "s:{{NAMESPACE}}:$NAMESPACE:g" config/operators/amq-operator-subscription.yaml | oc apply -f -
fi
log "Installing the AMQ Streams operator"
sed "s:{{NAMESPACE}}:$NAMESPACE:g" config/operators/amq-streams-operator-subscription.yaml | oc apply -f -
log "Installing the Datagrid operator"
sed "s:{{NAMESPACE}}:$NAMESPACE:g" config/operators/datagrid-subscription.yaml | oc apply -f -
log "Installing the Camel-K operator"
sed "s:{{NAMESPACE}}:$NAMESPACE:g" config/operators/camel-k-operator-subscription.yaml | oc apply -f -

# wait for operators

if [[ "$DROGUE_IOT" != true ]]; then
  wait_for_operator "AMQ Broker" amq-broker-rhel8
fi
wait_for_operator "AMQ Streams" amq-streams
wait_for_operator "Datagrid" datagrid
wait_for_operator "Camel-K" red-hat-camel-k

fi

log "Installing the infra Helm release: $HELM_INFRA_RELEASE_NAME"
INFRA_OPTS=("-f" "helm/bobbycar-core-infra/values.yaml")
if [[ "$DROGUE_IOT" == true ]]; then
  INFRA_OPTS+=("-f" "helm/bobbycar-core-infra/values.drogue.yaml")
  INFRA_OPTS+=("--set-string" "global.domain=-${NAMESPACE}.${APP_DOMAIN}")
  helm repo add drogue-iot https://drogue-iot.github.io/drogue-cloud-helm-charts/
  helm dependency build helm/bobbycar-core-infra
fi

helm upgrade --install "$HELM_INFRA_RELEASE_NAME" helm/bobbycar-core-infra/ \
"${INFRA_OPTS[@]}" \
--set-string namespace="$NAMESPACE" \
--set-string ocpDomain="$APP_DOMAIN" \
--set drogueIoT="$DROGUE_IOT"

sleep 30

if [[ "$DROGUE_IOT" != true ]]; then
log "Waiting for AMQ Broker pod"
oc wait --for=condition=Ready pod/bobbycar-amq-mqtt-ss-0 --timeout 300s
fi
log "Waiting for Datagrid pod"
oc wait --for=condition=Ready pod/bobbycar-dg-0 --timeout 300s
log "Waiting for Kafka Broker pod"
oc wait --for=condition=Ready pod/bobbycar-cluster-kafka-0 --timeout 300s
log "Waiting for Kafka Bridge pod"
[[ "$DROGUE_IOT" != true ]] && oc wait --for=condition=Available deployment/bobbycar-bridge --timeout 300s

log "Installing the apps Helm release: $HELM_APP_RELEASE_NAME"
APPS_OPTS=("-f" "helm/bobbycar-core-apps/values.yaml")
if [[ "$DROGUE_IOT" == true ]]; then
  APPS_OPTS+=("-f" "helm/bobbycar-core-apps/values.drogue.yaml")
fi
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

log "Installation completed! Open the Bobbycar dashboard and get started:"
oc get route dashboard -o json | jq -r .spec.host
