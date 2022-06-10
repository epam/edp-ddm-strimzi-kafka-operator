void call() {

    def newKafkaBrokerReplicas = "${env.REPFACTOR}".toInteger()
    sh(script: "helm get values registry-configuration -n $NAMESPACE > /tmp/platform-values.yaml")
    def platformValues = readYaml file: "/tmp/platform-values.yaml"
    def oldKafkaBrokerReplicas = platformValues["global"]["kafkaOperator"]["replicationFactor"].toInteger()

    if (newKafkaBrokerReplicas != oldKafkaBrokerReplicas) {
        def kafkaTopicList = sh(script: "oc exec kafka-cluster-kafka-0 -n $NAMESPACE -c kafka -- bin/kafka-topics.sh " +
                "--list --bootstrap-server kafka-cluster-kafka-bootstrap:9092", returnStdout: true).tokenize()
        if (newKafkaBrokerReplicas > 0) newKafkaBrokerReplicas = newKafkaBrokerReplicas.toInteger() - 1
        def kafkaBrokersIDs = sh(script: "seq 0 $newKafkaBrokerReplicas", returnStdout: true).tokenize()
        def reassignTopicReplicas = []
        kafkaTopicList.each { kafkaTopic ->
            def kafkaTopicPartitions = sh(script: "oc exec kafka-cluster-kafka-0 -n $NAMESPACE -c kafka -- bin/kafka-topics.sh " +
                    "--bootstrap-server kafka-cluster-kafka-bootstrap:9092 --describe --topic $kafkaTopic" +
                    "| grep -o -P '(?<=PartitionCount:).*(?=ReplicationFactor)'", returnStdout: true).trim().toInteger()
            for (int i in 0..kafkaTopicPartitions - 1) {
                reassignTopicReplicas.add("{\\\"topic\\\": \\\"$kafkaTopic\\\",\\\"partition\\\":$i,\\\"replicas\\\":$kafkaBrokersIDs}")
            }
        }
        def reassignTopicReplicasJson = "{\\\"version\\\": 1,\\\"partitions\\\": $reassignTopicReplicas}"
        sh "mkdir reassignment || true"
        sh "echo $reassignTopicReplicasJson > reassignment/reassignment.json"
        sh "oc rsync reassignment kafka-cluster-kafka-0:/tmp -n $NAMESPACE"

        if (newKafkaBrokerReplicas < oldKafkaBrokerReplicas) {
            sh "oc exec kafka-cluster-kafka-0 -n $NAMESPACE -c kafka -- bin/kafka-reassign-partitions.sh " +
                    "--bootstrap-server kafka-cluster-kafka-bootstrap:9092 " +
                    "--reassignment-json-file /tmp/reassignment/reassignment.json " +
                    "--execute"
        }
    }
}

return this;
