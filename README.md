
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
To access to swagger-ui, use this url : [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  

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
fr.insee.pearljam.application.guestOU=OU-POLE

#Database configuration
fr.insee.pearljam.persistence.database.host = pearljam-db
fr.insee.pearljam.persistence.database.port = 5432
fr.insee.pearljam.persistence.database.schema = pearljam
fr.insee.pearljam.persistence.database.user = pearljam
fr.insee.pearljam.persistence.database.password = pearljam
fr.insee.pearljam.persistence.database.driver = org.postgresql.Driver
fr.insee.pearljam.defaultSchema=public

#Datacollection service config
fr.insee.pearljam.datacollection.service.url.scheme=http
fr.insee.pearljam.datacollection.service.url.host=localhost
fr.insee.pearljam.datacollection.service.url.port=8081

#Mail service config
fr.insee.pearljam.mail.service.url.scheme=http
fr.insee.pearljam.mail.service.url.host=localhost
fr.insee.pearljam.mail.service.url.port=8082
fr.insee.pearljam.mail.service.recipients.list=pearl@pearljam.fr,jam@pearljam.fr
fr.insee.pearljam.mail.service.url.login=pearljam
fr.insee.pearljam.mail.service.url.password=pearljam

#Keycloak configuration
keycloak.realm=insee-realm
keycloak.resource=pearljam-web
keycloak.auth-server-url=http://localhost:8180/auth
keycloak.public-client=true
keycloak.bearer-only=true
keycloak.principal-attribute:preferred_username

#Keycloak roles
fr.insee.pearljam.interviewer.role=investigator
fr.insee.pearljam.admin.role=admin
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
To access to swagger-ui, use this url : [http://localhost:8080/pearljam/swagger-ui.html](http://localhost:8080/pearljam/swagger-ui.html)  
To access to keycloak, use this url : [http://localhost:8180](http://localhost:8180)  

## Before you commit
Before committing code please ensure,  
1 - README.md is updated  
2 - A successful build is run and all tests are sucessful  
3 - All newly implemented APIs are documented  
4 - All newly added properties are documented  

## End-Points
### Campaign-Controller
- `POST /api/campaign` : Post Campaign
- `DELETE /api/campaign/{id}` : Delete Campaign
- `PUT /api/campaign/{id}/collection-dates` : Put campaignCollectionDates
- `GET /api/campaign/{id}/interviewers` : Get interviewers
- `GET /api/campaign/{id}/survey-units/abandoned` : Get numberSUAbandoned
- `GET /api/campaign/{id}/survey-units/not-attributed` : Get numberSUNotAttributed
- `PUT /api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility` : Change visibility of a campaign for an Organizational Unit
- `GET /api/campaigns` : Get Campaigns

### Closing-Cause-Controller
- `GET /api/campaign/{id}/survey-units/interviewer/{idep}/closing-causes` : Get interviewerStateCount

### Contact-Outcome-Controller
- `GET /api/campaign/{id}/survey-units/contact-outcomes` : Get campaignStateCount
- `GET /api/campaign/{id}/survey-units/interviewer/{idep}/contact-outcomes` : Get contact-outcome type for an interviewer on a specific campaign
- `GET /api/campaign/{id}/survey-units/not-attributed/contact-outcomes` : Get Contact-outcomes count for non attributted SUs
- `GET /api/campaigns/survey-units/contact-outcomes` : Get campaignStateCount

### Data-Set-Controller
- `POST /api/create-dataset` : Create dataset
- `DELETE /api/delete-dataset` : Delete dataset

### Interviewer-Controller
- `GET /api/interviewer/{id}/campaigns` : Get interviewer campaigns
- `GET /api/interviewers` : Get interviewers
- `POST /api/interviewers` : Post interviewers

### Message-Controller
- `POST /api/mail` : Send mail to admins defined in properties
- `POST /api/message` : Post a message
- `GET /api/message-history` : Get the message history
- `PUT /api/message/{id}/interviewer/{idep}/delete` : Mark a message as deleted
- `PUT /api/message/{id}/interviewer/{idep}/read` : Mark a message as read
- `GET /api/messages/{id}` : Get a message
- `POST /api/verify-name` : Update Messages with campaigns or interviewers listed in request body

### Organization-Unit-Controller
- `DELETE /api/organization-unit/{id}` : Delete an Organization-unit
- `POST /api/organization-unit/{id}/users` : add users to an organization-unit
- `GET /api/organization-units` : Get all organization-units
- `POST /api/organization-units` : Create Context with Organizational Unit and users associated

### Preference-Controller
- `PUT /api/preferences` : Update preferences with campaigns listed in request body

### State-Controller
- `GET /api/campaign/{id}/survey-units/interviewer/{idep}/state-count` : Get interviewerStateCount
- `GET /api/campaign/{id}/survey-units/not-attributed/state-count` : Get state count for non attributted SUs
- `GET /api/campaign/{id}/survey-units/state-count` : Get campaignStateCount
- `GET /api/campaigns/survey-units/state-count` : Get campaignStateCount
- `GET /api/interviewers/survey-units/state-count` : Get interviewersStateCount

### Survey-Unit-Controller
- `GET /api/campaign/{id}/survey-units` : Update the Survey Unit
- `GET /api/check-habilitation` : Check habilitation
- `GET /api/survey-unit/{id}` : Get detail of specific survey unit
- `PUT /api/survey-unit/{id}` : Update the Survey Unit
- `PUT /api/survey-unit/{id}/close/{closingCause}` : Closes a survey unit
- `PUT /api/survey-unit/{id}/closing-cause/{closingCause}` : Add Closing cause
- `PUT /api/survey-unit/{id}/comment` : Update the state of Survey Units listed in request body
- `PUT /api/survey-unit/{id}/state/{state}` : Update the state of Survey Units listed in request body
- `GET /api/survey-unit/{id}/states` : Get states of given survey unit
- `PUT /api/survey-unit/{id}/viewed` : Update the state of Survey Units listed in request body
- `GET /api/survey-units` : Get SurveyUnits
- `POST /api/survey-units` : POST SurveyUnit assignations to interviewer
- `GET /api/survey-units/closable` : Get closable survey units
- `POST /api/survey-units/interviewers` : Post SurveyUnits
- `DELETE /api/survey-unit/{id}` : Delete SurveyUnit


### User-Controller
- `GET /api/user` : Get User
- `DELETE /api/user/{id}` : Delete User

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
- Paul Guillemet (paul.guillemet@keyconsulting.fr)

## License
Please check [LICENSE](https://github.com/InseeFr/Pearl-Jam-Back-Office/blob/master/LICENSE) file
