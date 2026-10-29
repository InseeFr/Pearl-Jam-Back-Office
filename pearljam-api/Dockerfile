FROM eclipse-temurin:25.0.5_8-jre-jammy@sha256:659b528cca74a0edc5c25315ec5819f35bce40bbcec645c4c534987e4c6be7c1

WORKDIR /opt/app

COPY ./target/*.jar /opt/app/app.jar

RUN groupadd -g 1000 tomcatgroup && \
    useradd -u 1000 -g tomcatgroup -s /usr/sbin/nologin -m tomcatuser && \
    chown -R 1000:1000 /opt/app

USER 1000

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]