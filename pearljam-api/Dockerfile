FROM eclipse-temurin:25.0.4_7-jre-jammy@sha256:10c251954d0bfe1a59ba93505f8c628d755919412400aa98685764c9353605d6

WORKDIR /opt/app

COPY ./target/*.jar /opt/app/app.jar

RUN groupadd -g 1000 tomcatgroup && \
    useradd -u 1000 -g tomcatgroup -s /usr/sbin/nologin -m tomcatuser && \
    chown -R 1000:1000 /opt/app

USER 1000

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]