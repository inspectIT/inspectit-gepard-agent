#!/bin/bash
  java  \
        -javaagent:build/libs/opentelemetry-javaagent.jar \
        -Dotel.service.name="my-service" \
        -Dinspectit.config.http.url="https://localhost:8080/api/v1" \
        -Djavax.net.ssl.trustStore="src/main/resources/keystore/inspectit-dev-keystore.jks" \
        -Djavax.net.ssl.trustStorePassword="Ocecat24" \
        -Dotel.javaagent.configuration-file=otel-configuration.properties \
        -jar /Users/lkr/Documents/Projects/VHV/02_ocelot/java-21-rest-example/build/libs/java-21-rest-example-0.0.1-SNAPSHOT.jar

#-agentlib:jdwp="transport=dt_socket,server=y,suspend=y,address=127.0.0.1:8000"\