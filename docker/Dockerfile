# lightweight base image
FROM busybox

COPY . .

RUN ln /opentelemetry-javaagent.jar /javaagent.jar && chmod -R go+r /javaagent.jar

ENTRYPOINT ["sh", "/entrypoint.sh"]
