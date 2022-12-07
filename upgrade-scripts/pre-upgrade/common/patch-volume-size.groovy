void call() {
    String valuesFile = '/tmp/platform-values.yaml'
    sh(script: "helm get values registry-auth -n ${NAMESPACE} > ${valuesFile}")
    LinkedHashMap platformValues = readYaml file: valuesFile
    ['kafka', 'zookeeper'].each {
        String storageSize = platformValues['global']['kafkaOperator']['storage']["${it}"]['size']
        ArrayList<String> volumes = sh(script: "oc get pvc -l strimzi.io/name=kafka-cluster-${it} --no-headers " +
                "-o custom-columns=NAME:.metadata.name -n ${NAMESPACE}",
                returnStdout: true).tokenize('\n')
        volumes.each { volume ->
            sh("oc patch pvc ${volume} --type merge -n ${NAMESPACE} " +
                    "-p '{\"spec\":{\"resources\":{\"requests\":{\"storage\":\"${storageSize}\"}}}}'")
        }
    }
}

return this;
