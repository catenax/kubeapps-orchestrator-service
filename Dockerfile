FROM openjdk:18

RUN apk update && apk add maven && apk add --upgrade maven

WORKDIR /app

COPY . /app

RUN mvn clean install -Dmaven.test.skip=true 

RUN mvn package

RUN mv orchestrator-service-0.0.1-SNAPSHOT.jar orchestrator-service.jar 

ENTRYPOINT ["java","-jar","orchestrator-service.jar"]

EXPOSE 9999
