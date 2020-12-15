FROM quay.io/strimzi/kafka:0.28.0-kafka-3.0.0
USER root:root
RUN mkdir -p /opt/kafka/plugins/confluentinc-kafka-connect-jdbc
COPY ./plugins/ /opt/kafka/plugins/confluentinc-kafka-connect-jdbc/
COPY ./libs/ /opt/kafka/libs/
USER 1001
