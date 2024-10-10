# inspectIT Gepard - Bootstrap

This project contains interfaces which have to be implemented by inspectIT Gepard Java agents and
are injected into the user application.

The resulting Jar file of this package will be pushed to the JVM's bootstrap class loader.
This is necessary in order to create a bridge between the user (application) classes 
and the classes which will be loaded by the OpenTelemetry extension class loader 
(classes of the inspectIT Gepard Java agent). 

**There should be NO dependencies in this submodule!**
