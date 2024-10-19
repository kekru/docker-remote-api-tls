FROM docker:27.3.1-cli as docker
FROM maven:3.6.3-openjdk-8-slim

COPY --from=docker --chmod=555 /usr/local/bin/docker /usr/local/bin/docker
COPY --from=docker --chmod=555 /usr/local/libexec/docker/cli-plugins/ /root/.docker/cli-plugins

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .

CMD mvn test
