FROM eclipse-temurin:21.0.8_9-jre-alpine@sha256:4ca7eff3ab0ef9b41f5fefa35efaeda9ed8d26e161e1192473b24b3a6c348aef

WORKDIR /opt/app/
COPY ./target/*.jar /opt/app/app.jar

# Setup a non-root user context (security)
RUN addgroup -g 1000 tomcatgroup
RUN adduser -D -s / -u 1000 tomcatuser -G tomcatgroup
RUN chown -R 1000:1000 /opt/app

USER 1000

ENTRYPOINT ["java", "-jar",  "/opt/app/app.jar"]