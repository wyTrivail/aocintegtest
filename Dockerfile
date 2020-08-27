# this dockerfile is used for github action
FROM gradle:6.5.0-jdk11 as builder

# build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

# settle down the executable
FROM adoptopenjdk:11-jdk-hotspot
COPY --from=builder /home/gradle/src/build/distributions/aocintegtest.tar /app/
WORKDIR /app
RUN tar -xvf aocintegtest.tar

COPY entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
