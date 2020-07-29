# kafka-streaming-service

## Dev

`kamel run src/main/java/com/redhat/bobbycar/routes/KafkaToDatagridRoute.java --dev --secret=bobbycar-kafka2datagrid-secret --profile=openshift`

## Properties

- com.redhat.bobbycar.camelk.dg.car.snapshot.cacheName: carsnapshots
- com.redhat.bobbycar.camelk.dg.car.cacheName: cars
- com.redhat.bobbycar.camelk.dg.zone.cacheName: zones
- com.redhat.bobbycar.camelk.dg.host: bobbycar-dg
- com.redhat.bobbycar.camelk.dg.password: TBD
- com.redhat.bobbycar.camelk.dg.user: operator
- com.redhat.bobbycar.camelk.kafka.brokers: bobbycar-cluster-kafka-brokers
- com.redhat.bobbycar.camelk.kafka.topic: bobbycar-gps
- com.redhat.bobbycar.camelk.mqtt.brokerUrl: tcp://bobbycar-amq-mqtt-all-0-svc:61616
- com.redhat.bobbycar.camelk.mqtt.topic: bobbycar/zonechange