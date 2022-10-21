void call() {
    String kafkaBrokerPod = "kafka-cluster-kafka-0"
    String kafkaBootstrapServer = "kafka-cluster-kafka-bootstrap:9092"
    String kafkaTopics = 'read-settings-inbound,read-settings-outbound,update-settings-inbound,' +
            'update-settings-outbound,read-settings-by-keycloak-id-inbound,read-settings-by-keycloak-id-outbound,' +
            'read-settings-by-keycloak-id-inbound.DLT,update-settings-inbound.DLT,read-settings-inbound.DLT'
    try {
        sh(script: "oc exec -n ${NAMESPACE} ${kafkaBrokerPod} -c kafka -- bin/kafka-topics.sh " +
                "--bootstrap-server ${kafkaBootstrapServer} --delete --topic ${kafkaTopics}")
    } catch (any) {
        println("WARN: Failed to remove kafka topics or topics do not exist")
    }
}

return this;
