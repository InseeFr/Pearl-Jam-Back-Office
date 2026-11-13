FROM eclipse-temurin:25.0.5_8-jre-jammy@sha256:a4e9fb35b48412cafbf036dabe201f22ee5e96f789e975bd6866ba0cbec45d17

WORKDIR /opt/app

COPY ./target/*.jar /opt/app/app.jar

RUN groupadd -g 1000 tomcatgroup && \
    useradd -u 1000 -g tomcatgroup -s /usr/sbin/nologin -m tomcatuser && \
    chown -R 1000:1000 /opt/app

USER 1000

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]