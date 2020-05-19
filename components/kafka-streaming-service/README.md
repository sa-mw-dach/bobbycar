# kafka-streaming-service

## Dev

`kamel run src/main/java/com/redhat/bobbycar/routes/KafkaSseRoute.java --dev --property com.redhat.bobbycar.camelk.kafka.topic=bobbycar-gps -d camel-undertow`