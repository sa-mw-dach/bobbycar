{{/*
* Set env-var settings for SmallRye MQTT connector channel.
*
* Arguments (dict):
*  * root - The root context (.)
*  * direction - 'incoming' or 'outgoing'
*  * channel - The name of the channel
*/}}
{{- define "mqtt.env-vars" }}
- name: mp.messaging.{{ .direction }}.{{ .channel }}.connector
  value: smallrye-mqtt
- name: mp.messaging.{{ .direction }}.{{ .channel }}.max-message-size
  value: "32768"
- name: mp.messaging.{{ .direction }}.{{ .channel }}.host
{{- with .root.Values.drogue.endpoints.mqtt.host }}
  value: {{ . | quote }}
{{- else }}
  value: {{ default "mqtt-endpoint" | quote }}
{{- end }}
- name: mp.messaging.{{ .direction }}.{{ .channel }}.port
  value: {{ .root.Values.drogue.endpoints.mqtt.port | quote }}
- name: mp.messaging.{{ .direction }}.{{ .channel }}.username
  value: $(com.redhat.bobbycar.carsim.instance)-gw@{{ .root.Values.drogue.application }}
- name: mp.messaging.{{ .direction }}.{{ .channel }}.password
  value: {{ .root.Values.drogue.gateway.password | quote }}


- name: mp.messaging.{{ .direction }}.{{ .channel }}.ssl
  value: "true"
- name: mp.messaging.{{ .direction }}.{{ .channel }}.ssl.truststore.location
  value: /etc/drogue/ca/truststore.p12
- name: mp.messaging.{{ .direction }}.{{ .channel }}.ssl.truststore.password
  value: "SetecAstronomy"
- name: mp.messaging.{{ .direction }}.{{ .channel }}.ssl.truststore.type
  value: "PKCS12"
{{- end }}