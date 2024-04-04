FROM tomcat:9-jre11-temurin

RUN rm -rf $CATALINA_HOME/webapps/*
COPY pearljambo.properties log4j2.xml $CATALINA_HOME/webapps/
ADD /target/*.war $CATALINA_HOME/webapps/ROOT.war