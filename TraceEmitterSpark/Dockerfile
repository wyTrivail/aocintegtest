# this dockerfile is used for github action
FROM gradle:6.5.0-jdk8 as builder

# build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build
RUN ls -R
RUN tar -xvf /home/gradle/src/build/distributions/TraceEmitter.tar

# settle down the executable
FROM amazoncorretto:8
COPY --from=builder /home/gradle/src/TraceEmitter /app/
COPY --from=builder /home/gradle/src/otelagent.jar /app/
WORKDIR /app

ENV OTEL_OTLP_ENDPOINT localhost:55680
ENV OTEL_RESOURCE_ATTRIBUTES 'service.name=OTIntegTest'
ENV JAVA_OPTS "-javaagent:/app/otelagent.jar"

EXPOSE 4567

CMD /app/bin/TraceEmitter
