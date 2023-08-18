{{- define "kafkaui.url" -}}
{{ printf "kafka-ui-%s.%s" .Release.Namespace .Values.dnsWildcard }}
{{- end }}
