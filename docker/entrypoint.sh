rm -rf agent/*
# Copy agent jar into shared volume
mkdir agent
cp inspectit-gepard-agent.jar agent/inspectit-gepard-agent.jar
# Keep the container running
while true; do sleep 2; done;
