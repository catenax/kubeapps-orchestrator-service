FROM openjdk:19-jdk-alpine3.16

RUN apk update && apk add maven && apk add --upgrade maven

WORKDIR /app

COPY . /app

RUN mvn clean install -Dmaven.test.skip=true 

WORKDIR target

#RUN mv kubeapps-wrapper-0.0.1.jar orchestrator-service.jar 

ENTRYPOINT ["java","-jar","auto-setup-0.0.1.jar"]

EXPOSE 9999
