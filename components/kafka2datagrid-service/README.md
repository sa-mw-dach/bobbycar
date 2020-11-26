# kafka2datagrid service

- Stores BobbycarZone CRs in Datagrid (every 60 seconds)
- Stores car events from Kafka in Datagrid (GPS data)
- Stores Zone change detection events in MQTT Broker

## Start integration in dev mode

`kamel run src/main/java/com/redhat/bobbycar/routes/KafkaToDatagridRoute.java --dev --name k2dgDEV --secret=kafka2datagrid-secret --profile=openshift`

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
- com.redhat.bobbycar.camelk.dg.namespace: bobbycar
- com.redhat.bobbycar.camelk.dg.ocp.api: api.ocp4.stormshift.coe.muc.redhat.com
