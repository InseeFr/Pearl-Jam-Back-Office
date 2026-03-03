FROM eclipse-temurin:21.0.10_7-jre-alpine@sha256:6ad8ed080d9be96b61438ec3ce99388e294af216ed57356000c06070e85c5d5d

WORKDIR /opt/app/
COPY ./target/*.jar /opt/app/app.jar

# Setup a non-root user context (security)
RUN addgroup -g 1000 tomcatgroup
RUN adduser -D -s / -u 1000 tomcatuser -G tomcatgroup
RUN chown -R 1000:1000 /opt/app

USER 1000

ENTRYPOINT ["java", "-jar",  "/opt/app/app.jar"]