# inspectIT Gepard Extension

Extension for the OpenTelemetry Java agent.

inspectIT Gepard is the further development of [inspectIT Ocelot](https://github.com/inspectIT/inspectit-ocelot).
While the inspectIT Ocelot Java agent is self-made, inspectIT Gepard uses the OpenTelemetry Java agent as basis
and extends it with features from inspectIT Ocelot.

## Main Features

We want to enable **dynamic instrumentation** with the OpenTelemetry agent. 
This means, that you can change your instrumentation **during runtime**. 
For example, you can add new methods to your tracing or record data for new metrics, everything without restarting your application!

The instrumentation configuration will be fetched from a remote server regularly. 
The server's url is configurable via the property `inspectit.config.http.url`

We are also developing our own [configuration server](https://github.com/inspectIT/inspectit-gepard-configurationserver).

## Installation

To build this extension project, run `./gradlew build` or `./gradlew extendedAgent` (no tests). 
You can find the resulting jar file in `build/libs/`.

To add the extension to the instrumentation agent:

1. Copy the jar file to a host that is running an application to which you've attached the OpenTelemetry Java instrumentation.
2. Modify the startup command to add the full path to the extension file. For example:

     ```bash
     java -javaagent:path/to/opentelemetry-javaagent.jar \
          -Dotel.javaagent.extensions=build/libs/opentelemetry-javaagent.jar \
          -Dotel.service.name="my-service"
          -jar myapp.jar
     ```

Note: to load multiple extensions, you can specify a comma-separated list of extension jars or directories (that
contain extension jars) for the `otel.javaagent.extensions` value.

## Network communication

The extension contains a client, who is able to communicate with other servers via HTTPS.
You can set the server url via system or environmental properties.
You have the option to use a keystore for TLS:

1. Provide a local keystore, which contains the certificate of your server
2. Modify the startup command to add the path to the keystore as well as the password. For example:

   ```bash
     java -javaagent:path/to/opentelemetry-javaagent.jar \
          -Dotel.javaagent.extensions=build/libs/opentelemetry-javaagent.jar \
          -Dotel.service.name="my-service" \
          -Dinspectit.config.http.url="https://{server-host:port}/api/v1" \
          -Djavax.net.ssl.trustStore="path\to\keystore\agent-keystore.jks" \
          -Djavax.net.ssl.trustStorePassword="password"
          -jar myapp.jar
     ```

## Further Information

The repository was build upon this example project: https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/examples/extension

## Docker

To push a new Docker image, run the following commands:


1. `./gradlew dockerTag`
2. `docker login -u <user> -p <password>`
3. `docker push inspectit/inspectit-gepard-agent:<version>`


### Why Gepard?
Gepard is the German name for the animal cheetah as well as an acronym for: 

"**G**anzheitliche, **e**ffizienz-orientierte, **P**erformance **A**nwendungsüberwachung mit **R**eporting und **D**iagnose",

which means: holistic, efficiency-orientated, performance application monitoring with reporting and diagnostics.

### Limitations

Currently, it is not tested to instrument methods of **lambdas** or synthetic methods.
