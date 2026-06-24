FROM eclipse-temurin:25.0.3_9-jre-jammy@sha256:c3e62cd0cece58d8de8d760ab95a5014f3b5a6ea32178f54270edb5b4aab9d1f

WORKDIR /opt/app

COPY ./target/*.jar /opt/app/app.jar

RUN groupadd -g 1000 tomcatgroup && \
    useradd -u 1000 -g tomcatgroup -s /usr/sbin/nologin -m tomcatuser && \
    chown -R 1000:1000 /opt/app

USER 1000

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]