FROM eclipse-temurin:21.0.6_7-jre-alpine@sha256:4c07db858c3b8bfed4cb9163f4aeedbff9c0c2b8212ec1fa75c13a169dec8dc6

WORKDIR /opt/app/
COPY ./target/*.jar /opt/app/app.jar

# Setup a non-root user context (security)
RUN addgroup -g 1000 tomcatgroup
RUN adduser -D -s / -u 1000 tomcatuser -G tomcatgroup
RUN chown -R 1000:1000 /opt/app

USER 1000

ENTRYPOINT ["java", "-jar",  "/opt/app/app.jar"]