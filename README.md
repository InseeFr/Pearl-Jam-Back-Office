
# Pearl Jam Back-Office
Back-office services for Pearl Jam  
REST API for communication between Pearl Jam DB and Pearl Jam UI.

## Requirements
For building and running the application you need:
- [JDK 21](https://jdk.java.net/archive/)
- Maven 3  

## Install and excute unit tests
Use the maven clean and maven install 
```shell
mvn clean install
```  

## Running the application locally
```shell
mvn spring-boot:run
```  

## Deployment
### 1. Package the application
```shell
mvn clean package
```  
The jar will be generated in `/target` repository

### 2. Launch app with embedded tomcat
```shell
java -jar app.jar
```

### 3. Application Access
To access the swagger-ui, use this url : [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Docker/Kubernetes

A Dockerfile is present on this root project to deploy a container. You can [get docker images on docker hub](https://hub.docker.com/r/inseefr/pearl-jam-back-office)

[Helm chart repository](https://github.com/InseeFr/Helm-Charts/) is available for the pearl jam backoffice/db/frontend


## Liquibase
Liquibase is enabled by default and run changelogs if needed.

#### Properties
Minimal configuration for dev purpose only (no auth)
User is considered as authenticated admin user

```yaml  
application:
  roles:
    interviewer:
    local_user:
    national_user:
    admin: admin_user
    webclient:
feature:
  oidc:
    enabled: false
  swagger:
    enabled: true
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pearljam
    username: 
    password:
    driver-class-name: org.postgresql.Driver
  liquibase:
    contexts: dev
    changeLog: classpath:db/demo.xml
  messages:
    cache-seconds: 1
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: false
  jackson:
    serialization:
      indent-output: true

logging:
  level:
    org:
      hibernate:
        SQL: WARN
        type:
          descriptor:
            sql:
              BasicBinder: WARN
    liquibase: INFO
```

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
- `GET /campaigns/{id}/ongoing` campaign is ongoing
- `GET /api/campaigns/ongoing` : Campaigns is ongoing (BATCH extraction-transfert-synchronisation)

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
- spring-boot-data-jpa
- spring-boot-security
- spring-boot-web
- spring-boot-tomcat
- spring-boot-test
- spring-boot-starter-oauth2-resource-server
- liquibase
- postgresql
- h2 (tests)
- junit
- springdoc

## License
Please check [LICENSE](https://github.com/InseeFr/Pearl-Jam-Back-Office/blob/master/LICENSE) file
