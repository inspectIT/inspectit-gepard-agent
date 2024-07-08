rm -rf /agent/*
# Copy agent jar into shared volume
cp /opentelemetry-javaagent.jar /agent/opentelemetry-javaagent.jar
# Keep the container running
while true; do sleep 2; done;
