# lightweight base image
FROM busybox

COPY . .

RUN chmod go+r inspectit-gepard-agent.jar

ENTRYPOINT ["sh", "/entrypoint.sh"]
