void call() {

  ["kafka-cluster-network-policy-kafka", "kafka-cluster-network-policy-zookeeper", "kafka-connect-cluster-connect"].each { it ->
      sh "oc delete networkpolicy $it -n $NAMESPACE || true"
  }

}

return this;
