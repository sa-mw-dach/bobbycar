#!/bin/bash

set -e

log() {
  echo
  echo "##### $*"
}

source install_cleanup_vars.sh

if [[ "$INSTALL_OPERATORS" == true ]]; then
log "Creating namespace $NAMESPACE for Bobbycar demo"
oc new-project "$NAMESPACE" || true
log "Installing operator group"
sed "s:{{NAMESPACE}}:$NAMESPACE:g" config/operators/operator-group.yaml | oc apply -f -
log "Installing the AMQ Broker operator"
sed "s:{{NAMESPACE}}:$NAMESPACE:g" config/operators/amq-operator-subscription.yaml | oc apply -f -
log "Installing the AMQ Streams operator"
sed "s:{{NAMESPACE}}:$NAMESPACE:g" config/operators/amq-streams-operator-subscription.yaml | oc apply -f -
log "Installing the Datagrid operator"
sed "s:{{NAMESPACE}}:$NAMESPACE:g" config/operators/datagrid-subscription.yaml | oc apply -f -
log "Installing the Camel-K operator"
sed "s:{{NAMESPACE}}:$NAMESPACE:g" config/operators/camel-k-operator-subscription.yaml | oc apply -f -

sleep 30s

log "Waiting for AMQ Broker operator"
AMQ_BROKER_POD=$(oc get pod -l name=amq-broker-operator -o jsonpath="{.items[0].metadata.name}")
oc wait --for=condition=Ready pod/"$AMQ_BROKER_POD" --timeout 300s
log "Waiting for AMQ Streams operator"
AMQ_STREAMS_POD=$(oc get pod -l name=amq-streams-cluster-operator -o jsonpath="{.items[0].metadata.name}")
oc wait --for=condition=Ready pod/"$AMQ_STREAMS_POD" --timeout 300s
log "Waiting for Datagrid operator"
DATAGRID_POD=$(oc get pod -l name=infinispan-operator -o jsonpath="{.items[0].metadata.name}")
oc wait --for=condition=Ready pod/"$DATAGRID_POD" --timeout 300s
log "Waiting for Camel-K operator"
CAMEL_K_POD=$(oc get pod -l name=camel-k-operator -o jsonpath="{.items[0].metadata.name}")
oc wait --for=condition=Ready pod/"$CAMEL_K_POD" --timeout 300s
fi ;

log "Installing the infra Helm release: $HELM_INFRA_RELEASE_NAME"
helm install "$HELM_INFRA_RELEASE_NAME" --set-string namespace="$NAMESPACE" --set-string ocpDomain="$APP_DOMAIN" helm/bobbycar-core-infra/

sleep 30s

log "Waiting for AMQ Broker pod"
oc wait --for=condition=Ready pod/bobbycar-amq-mqtt-ss-0 --timeout 300s
log "Waiting for Kafka Broker pod"
oc wait --for=condition=Ready pod/bobbycar-cluster-kafka-0 --timeout 300s
log "Waiting for Datagrid pod"
oc wait --for=condition=Ready pod/bobbycar-dg-0 --timeout 300s
log "Waiting for Kafka Bridge pod"
oc wait --for=condition=Available deployment/bobbycar-bridge --timeout 300s

log "Installing the apps Helm release: $HELM_APP_RELEASE_NAME"
helm install "$HELM_APP_RELEASE_NAME" helm/bobbycar-core-apps \
--set-string ocpDomain="$APP_DOMAIN" \
--set-string ocpApi="$API_DOMAIN" \
--set-string namespace="$NAMESPACE" \
--set-string dashboard.config.googleApiKey="$GOOGLE_API_KEY"

sleep 30s

log "Waiting for Bobbycar pod"
oc wait --for=condition=Available dc/car-simulator --timeout 300s
log "Waiting for Bobbycar Dashboard pod"
oc wait --for=condition=Available dc/dashboard --timeout 300s
log "Waiting for Dashboard Streaming service pod"
oc wait --for=condition=Available deployment/dashboard-streaming --timeout 300s

log "Waiting for Camel-K integrations to complete..."
oc wait --for=condition=Ready integration/cache-service --timeout 1800s
oc wait --for=condition=Ready integration/kafka2datagrid --timeout 1800s
oc wait --for=condition=Ready integration/mqtt2kafka --timeout 1800s

log "Installation completed! Open the Bobbycar dashboard and get started:"
oc get route dashboard -o json | jq -r .spec.host