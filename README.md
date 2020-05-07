
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
 
  
#### Define the JAVA_OPTS
Set the value of JAVA_OPTS  
```shell
JAVA_OPTS=-Dspring.profiles.active=prod -Dspring.config.location=classpath:/,${catalina.base}/webapps/insee.properties
``` 
This line will define the profile to use and the location of external properties.  

#### Properties file
In the classpath define before, create insee.properties and complete the following properties:  
```shell  
fr.insee.pearljam.application.mode=NoAuth #or KeyCloak or Basic
fr.insee.pearljam.application.crosOrigin=*

fr.insee.pearljam.persistence.database.host = localhost
fr.insee.pearljam.persistence.database.port = 5433
fr.insee.pearljam.persistence.database.schema = schema_name
fr.insee.pearljam.persistence.database.user = user_name
fr.insee.pearljam.persistence.database.password = password
fr.insee.pearljam.persistence.database.driver = org.postgresql.Driver

fr.insee.qpearljamueen.logging.path=classpath:log4j2.xml
fr.insee.pearljam.defaultSchema=public

keycloak.realm=PearlJamAPI
keycloak.resource=PearlJamAPI
keycloak.auth-server-url=http://localhost:8180/auth
keycloak.ssl-required=external
keycloak.public-client=true
keycloak.principal-attribute:preferred_username	 
```  

### 4. Tomcat start
From a terminal navigate to tomcat/bin folder and execute  
```shell
catalina.bat run (on Windows)
```  
```shell
catalina.sh run (on Unix-based systems)
```  

### 5. Application Access
To access to swagger-ui, use this url : [http://localhost:8080/queen-0.0.1-SNAPSHOT/swagger-ui.html](http://localhost:8080/queen-0.0.1-SNAPSHOT/swagger-ui.html)  
To access to keycloak, use this url : [http://localhost:8180](http://localhost:8180)  

## Before you commit
Before committing code please ensure,  
1 - README.md is updated  
2 - A successful build is run and all tests are sucessful  
3 - All newly implemented APIs are documented  
4 - All newly added properties are documented  

## End-Points
- `GET /survey-units` : GET the list of survey unit for current user

- `GET /survey-unit/{id}` : GET the detail of specific survey unit 

- `PUT /survey-unit/{id}` : PUT the detail of specific survey unit  

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
