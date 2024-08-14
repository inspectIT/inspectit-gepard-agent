# lightweight base image
FROM busybox

COPY . .

RUN chmod go+r opentelemetry-javaagent.jar

ENTRYPOINT ["sh", "/entrypoint.sh"]
