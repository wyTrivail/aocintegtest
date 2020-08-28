FROM amazoncorretto:8

WORKDIR /app
COPY TraceEmitter /app/TraceEmitter
COPY aws-opentelemetry-agent-0.7.0.jar /app/

ENV OTEL_OTLP_ENDPOINT localhost:55680
ENV OTEL_RESOURCE_ATTRIBUTES 'service.name=OTIntegTest'
ENV JAVA_OPTS "-javaagent:/app/aws-opentelemetry-agent-0.7.0.jar -Dotel.imr.export.interval=1000"

EXPOSE 4567

COPY entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]


