{{- define "kafkaui.url" -}}
{{ printf "kafka-ui-%s.%s" .Release.Namespace .Values.dnsWildcard }}
{{- end }}

{{- define "imageRegistry" -}}
{{- if .Values.global.imageRegistry -}}
{{- printf "%s/" .Values.global.imageRegistry -}}
{{- else -}}
{{- end -}}
{{- end }}
