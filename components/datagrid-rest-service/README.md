# kafka-streaming-service

## Dev

`kamel run src/main/java/com/redhat/bobbycar/routes/DatagridToRestRoute.java --dev -d camel-undertow -d camel-infinispan --secret=bobbycar-kafka2datagrid-secret --profile=openshift`