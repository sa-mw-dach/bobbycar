#!/bin/bash

set -e

log() {
  echo
  echo "##### $*"
}

source install_cleanup_vars.sh
test -f .env && source .env

log "Uninstalling Helm release: $HELM_SERVERLESS_RELEASE_NAME"
# shellcheck disable=SC2015
helm uninstall "$HELM_SERVERLESS_RELEASE_NAME" && sleep 10 || true

log "Uninstalling Helm release: $HELM_APP_RELEASE_NAME"
# shellcheck disable=SC2015
helm uninstall "$HELM_APP_RELEASE_NAME" && sleep 10 || true

log "Uninstalling Helm release: $HELM_INFRA_RELEASE_NAME"
# shellcheck disable=SC2015
helm uninstall "$HELM_INFRA_RELEASE_NAME" && sleep 10 || true

log "Deleting namespace $NAMESPACE"
oc delete namespace "$NAMESPACE" --wait=true || true

log "Deleting Custom Resource Definition: BobbycarZone"
oc delete crd bobbycarzones.bobbycar.redhat.com || true

log "Uninstallation complete!!!"