# kafka-streaming-service

## Dev mode

````shell
kamel run src/main/java/com/redhat/bobbycar/routes/KafkaSseRoute.java --dev --name kafkaStreamingDev --secret=kafka-streaming --profile=openshift --trait service.enabled=false
````
