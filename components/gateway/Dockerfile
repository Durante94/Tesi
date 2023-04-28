FROM docker.io/maven:3.8.4-jdk-11 as builder
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -Dmaven.test.skip
RUN mkdir target/extracted \
    && java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

FROM docker.io/openjdk:11
WORKDIR /app

ARG EXTRACTED=/app/target/extracted
COPY --from=builder ${EXTRACTED}/dependencies/ ./
COPY --from=builder ${EXTRACTED}/spring-boot-loader/ ./
COPY --from=builder ${EXTRACTED}/snapshot-dependencies/ ./
COPY --from=builder ${EXTRACTED}/application/ ./

RUN set -ex && \
    chgrp -R 0 /app && \
    chmod g+rwX /app

EXPOSE 8080
ENTRYPOINT ["java","org.springframework.boot.loader.JarLauncher"]