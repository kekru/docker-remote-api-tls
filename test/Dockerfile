FROM maven:3.6.3-openjdk-8-slim

RUN curl https://download.docker.com/linux/static/stable/x86_64/docker-19.03.13.tgz | tar xvz --directory /tmp \
 && mv -v /tmp/docker/docker /usr/local/bin/docker \
 && chmod +x /usr/local/bin/docker \
 && rm -rf /tmp/docker

RUN curl -L "https://github.com/docker/compose/releases/download/1.27.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose \
 && chmod +x /usr/local/bin/docker-compose

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .

CMD mvn test
