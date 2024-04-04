FROM tomcat:9-jre11-temurin
# Note: Tomcat 10 is not compatible with Spring Boot 2!!

RUN rm -rf $CATALINA_HOME/webapps/*
COPY pearljambo.properties log4j2.xml $CATALINA_HOME/webapps/
ADD /target/*.war $CATALINA_HOME/webapps/ROOT.war