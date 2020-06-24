# this dockerfile is used for github action
FROM gradle:6.5.0-jdk8 as builder

# build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

# settle down the executable
FROM adoptopenjdk:8-jdk-hotspot
COPY --from=builder /home/gradle/src/build/distributions/aocintegtest.tar /app/
WORKDIR /app
RUN tar -xvf aocintegtest.tar

# run
WORKDIR /app/aocintegtest
CMD bin/aocintegtest
