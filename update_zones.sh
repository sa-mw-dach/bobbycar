#!/bin/bash

set -e

oc apply -f helm/bobbycar-core-apps/templates/bobbycar-zone-north.yaml
oc apply -f helm/bobbycar-core-apps/templates/bobbycar-zone-south.yaml