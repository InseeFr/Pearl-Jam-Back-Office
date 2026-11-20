FROM eclipse-temurin:25.0.5.1_2-jre-jammy@sha256:1c6798886da1ae157e6c751cedb80eacf15d883e651ffe9acdcc534c19f482de

WORKDIR /opt/app

COPY ./target/*.jar /opt/app/app.jar

RUN groupadd -g 1000 tomcatgroup && \
    useradd -u 1000 -g tomcatgroup -s /usr/sbin/nologin -m tomcatuser && \
    chown -R 1000:1000 /opt/app

USER 1000

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]