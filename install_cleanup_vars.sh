# NAMESPACE     # The namespace to deploy the demo environment in
# APP_DOMAIN    # The clusters app domain without port
# API_DOMAIN    # The clusters api domain without port

NAMESPACE=
APP_DOMAIN=
API_DOMAIN=

GOOGLE_API_KEY=         # Google Maps API key - https://developers.google.com/maps/documentation/javascript/get-api-key
OWM_WEATHER_API_KEY=    # OpenWeatherMap API Key - https://openweathermap.org/api
IBM_WEATHER_API_KEY=    # https://www.ibm.com/products/environmental-intelligence-suite/data-packages

INSTALL_KNATIVE=false       # Installs OpenShift Serverless in the cluster
INSTALL_OPERATORS=true      # Installs the required namespaced operators: AMQ Streams, AMQ Broker, Datagrid, Camel-K
DROGUE_IOT=true             # Installs Drogue Cloud - https://drogue.io - as IoT device connectivity layer (optional)
DELETE_CRD=false            # Deletes the BobbycarZone CRD when executing ./cleanup.sh

HELM_INFRA_RELEASE_NAME=infra
HELM_APP_RELEASE_NAME=apps
HELM_SERVERLESS_RELEASE_NAME=serverless