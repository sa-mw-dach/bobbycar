{{ define "tls.http.proto" }}
{{- if .Values.tls.enabled }}https{{- else -}}http{{- end }}
{{- end }}

{{ define "tls.ws.proto" }}
{{- if .Values.tls.enabled }}https{{- else -}}http{{- end }}
{{- end }}
