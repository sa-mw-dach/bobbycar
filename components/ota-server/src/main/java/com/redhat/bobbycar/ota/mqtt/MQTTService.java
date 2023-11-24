package com.redhat.bobbycar.ota.mqtt;

import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Startup
@ApplicationScoped
public class MQTTService {

    @ConfigProperty(name = "mqtt.paho.broker.uri", defaultValue = "tcp://localhost:1883")
    String broker;
    @ConfigProperty(name = "mqtt.paho.client-id", defaultValue = "ota-server")
    String clientId;

    Map<String, String> subscriptions = new ConcurrentHashMap<>();
    MqttClient mqttClient;

    @PostConstruct
    public void init() throws InterruptedException{
        try {
            this.connectBroker();
        } catch(MqttException me) {
            System.out.println("reason: "+me.getReasonCode());
            System.out.println("msg: "+me.getMessage());
            System.out.println("loc: "+me.getLocalizedMessage());
            System.out.println("cause: "+me.getCause());
            System.out.println("exception: "+me);
            me.printStackTrace();
            while(!this.mqttClient.isConnected()){
                Thread.sleep(5000);
                try {
                    this.connectBroker();
                } catch (Exception e) {
                    System.out.println("reason: "+me.getReasonCode());
                    System.out.println("msg: "+me.getMessage());
                    System.out.println("loc: "+me.getLocalizedMessage());
                    System.out.println("cause: "+me.getCause());
                    System.out.println("exception: "+me);
                }
                
            }
        }
    }

    private void connectBroker() throws MqttException{
        this.mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        System.out.println("Connecting to broker: "+broker);
        // this.mqttClient.setCallback(this);
        this.mqttClient.connect(connOpts);
        System.out.println("Successfully connected to MQTT broker!!");
    }

    public void sendMessage(String payload, String topic, int qos){
        MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
        message.setQos(qos);
        try {
            this.mqttClient.publish(topic, message);
        } catch (MqttException me){
            me.printStackTrace();
        }
    }

    public Set<String> listSubscriptions(){
        return this.subscriptions.keySet();
    }

    public void addSubscription(String topic){
        try {
            if(this.subscriptions.containsKey(topic)){
                return;
            }
            this.mqttClient.subscribe(topic);
            this.subscriptions.put(topic,topic);
        } catch (MqttException me){
            me.printStackTrace();
        }
    }

    public void removeSubscription(String topic){
        try {
            this.mqttClient.unsubscribe(topic);
            this.subscriptions.remove(topic);
        } catch (MqttException me){
            me.printStackTrace();
        }
    }

    public void removeAllSubscriptions(){
        try {
            for (Map.Entry<String,String> entry: subscriptions.entrySet()) {
                this.mqttClient.unsubscribe(entry.getKey());
            }
        } catch (MqttException me){
            me.printStackTrace();
        }
        this.subscriptions.clear();
    }

    @PreDestroy
    public void cleanup(){
        try {
            this.mqttClient.disconnect();
            System.out.println("Disconnected");
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }

}
