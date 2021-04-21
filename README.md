
# Pearl Jam Back-Office
Back-office services for Pearl Jam  
REST API for communication between Pearl Jam DB and Pearl Jam UI.

## Requirements
For building and running the application you need:
- [JDK 11](https://jdk.java.net/archive/)
- Maven 3  
- Docker for tests

## Install and excute unit tests
Use the maven clean and maven install 
```shell
mvn clean install
```  

## Running the application locally
Use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:  
```shell
mvn spring-boot:run
```  

## Application Accesses locally
To access to swagger-ui, use this url : [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)  

## Keycloak Configuration 
1. To start the server on port 8180 execute in the bin folder of your keycloak :
```shell
standalone.bat -Djboss.socket.binding.port-offset=100 (on Windows)

standalone.sh -Djboss.socket.binding.port-offset=100 (on Unix-based systems)
```  
2. Go to the console administration and create role investigator and a user with this role.


## Deploy application on Tomcat server
### 1. Package the application
Use the [Spring Boot Maven plugin]  (https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:  
```shell
mvn clean package
```  
The war will be generate in `/target` repository  

### 2. Install tomcat and deploy war
To deploy the war file in Tomcat, you need to : 
Download Apache Tomcat and unpackage it into a tomcat folder  
Copy your WAR file from target/ to the tomcat/webapps/ folder  

### 3. Tomcat config
Before to startup the tomcat server, some configurations are needed : 
 
#### External Properties file
Create pearljambo.properties near war file and complete the following properties:  
```shell  
#Profile configuration
spring.profiles.active=prod

#Logs configuration
fr.insee.pearljam.logging.path=${catalina.base}/webapps/log4j2.xml
fr.insee.pearljam.logging.level=DEBUG

#Application configuration
fr.insee.pearljam.application.mode=keycloak
fr.insee.pearljam.application.crosOrigin=*

#Database configuration
fr.insee.pearljam.persistence.database.host = pearljam-db
fr.insee.pearljam.persistence.database.port = 5432
fr.insee.pearljam.persistence.database.schema = pearljam
fr.insee.pearljam.persistence.database.user = pearljam
fr.insee.pearljam.persistence.database.password = pearljam
fr.insee.pearljam.persistence.database.driver = org.postgresql.Driver
fr.insee.pearljam.defaultSchema=public

#Keycloak configuration
keycloak.realm=insee-realm
keycloak.resource=pearljam-web
keycloak.auth-server-url=http://localhost:8180/auth
keycloak.public-client=true
keycloak.bearer-only=true
keycloak.principal-attribute:preferred_username

#Keycloak roles
fr.insee.pearljam.interviewer.role=investigator
fr.insee.pearljam.user.local.role=manager_local
fr.insee.pearljam.user.national.role=manager_national
```

#### External log file
Create log4j2.xml near war file and define your  external config for logs. 

### 4. Tomcat start
From a terminal navigate to tomcat/bin folder and execute  
```shell
catalina.bat run (on Windows)
```  
```shell
catalina.sh run (on Unix-based systems)
```  

### 5. Application Access
To access to swagger-ui, use this url : [http://localhost:8080/pearljam-1.1.0/swagger-ui.html](http://localhost:8080/pearljam-1.1.0/swagger-ui.html)  
To access to keycloak, use this url : [http://localhost:8180](http://localhost:8180)  

## Before you commit
Before committing code please ensure,  
1 - README.md is updated  
2 - A successful build is run and all tests are sucessful  
3 - All newly implemented APIs are documented  
4 - All newly added properties are documented  

## End-Points

Campaign :

- `GET /campaign/{id}/interviewers` : Get interviewers

- `GET /campaign/{id}/survey-units/abandoned` : Get numberSUAbandoned

- `GET /campaign/{id}/survey-units/contact-outcomes` : Get campaignStateCount

- `GET /campaign/{id}/survey-units/interviewer/{idep}/state-count` : Get interviewerStateCount

- `GET /campaign/{id}/survey-units/not-attributed` : Get numberSUNotAttributed

- `GET /campaign/{id}/survey-units/not-attributed/contact-outcomes` : Get Contact-outcomes count for non attributted SUs

- `GET /campaign/{id}/survey-units/not-attributed/state-count` : Get state count for non attributted SUs

- `GET /campaign/{id}/survey-units/state-count` : Get campaignStateCount

- `GET /campaigns` : Get Campaigns

- `GET /campaigns/survey-units/contact-outcomes` : Get campaignStateCount

- `GET /campaigns/survey-units/state-count` : Get campaignStateCount

- `GET /interviewers/survey-units/state-count` : Get interviewersStateCount

- `PUT /campaign/{id}/collection-dates` : Put campaignCollectionDates

- `PUT /campaign/{idCampaign}/organizational-unit/{idOu}/visibility` : Change visibility of a campaign for an Organizational Unit

Dataset :

- `POST /create-dataset` : Create dataset

- `DELETE /delete-dataset` : Delete dataset

Interviewer :

- `GET /campaign/{id}/survey-units/interviewer/{idep}/contact-outcomes` : Get contact-outcome type for an interviewer on a specific campaign

- `GET /interviewer/{id}/campaigns` : Get interviewer campaigns

- `GET /interviewers` : Get interviewers

Message :

- `GET /message-history` : Get the message history

- `GET /messages/{id}` : Get a message

- `PUT /message/{id}/interviewer/{idep}/delete` : Mark a message as deleted

- `PUT /message/{id}/interviewer/{idep}/read` : Mark a message as read

- `POST /message` : Post a message

- `POST /verify-name` : Update Messages with campaigns or interviewers listed in request body

Preference :

- `PUT /preferences` : Update preferences with campaigns listed in request body

SurveyUnit :

- `GET /campaign/{id}/survey-units` : Update the Survey Unit

- `GET /check-habilitation` : Check habilitation

- `GET /survey-unit/{id}` : Get detail of specific survey unit

- `GET /survey-unit/{id}/states` : Get states of given survey unit

- `GET /survey-units` : Get SurveyUnits

- `GET /survey-units/closable` : Get closable survey units

- `PUT /survey-unit/{id}` : Update the Survey Unit

- `PUT /survey-unit/{id}/close/{closingCause}` : Closes a survey unit

- `PUT /survey-unit/{id}/closing-cause/{closingCause}` : Add Closing cause

- `PUT /survey-unit/{id}/comment` : Update the state of Survey Units listed in request body

- `PUT /survey-unit/{id}/state/{state}` : Update the state of Survey Units listed in request body

- `PUT /survey-unit/{id}/viewed` : Update the state of Survey Units listed in request body

User :

- `GET /user` : Get User

## Libraries used
- spring-boot-jpa
- spring-boot-security
- spring-boot-web
- spring-boot-tomcat
- spring-boot-test
- rest-assured
- liquibase
- postgresql
- junit
- springfox-swagger2
- hibernate
- keycloak 

## Developers
- Benjamin Claudel (benjamin.claudel@keyconsulting.fr)
- Samuel Corcaud (samuel.corcaud@keyconsulting.fr)

## License
Please check [LICENSE](https://github.com/InseeFr/Pearl-Jam-Back-Office/blob/master/LICENSE) file
